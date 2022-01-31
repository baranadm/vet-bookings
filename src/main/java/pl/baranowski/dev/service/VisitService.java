package pl.baranowski.dev.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
import pl.baranowski.dev.exception.VetNotActiveException;
import pl.baranowski.dev.repository.PatientRepository;
import pl.baranowski.dev.repository.VetRepository;
import pl.baranowski.dev.repository.VisitRepository;

@Service
public class VisitService {

	@Autowired
	VisitRepository visitRepository;
	@Autowired
	PatientRepository patientRepository;
	@Autowired
	VetRepository vetRepository;
	@Autowired
	ModelMapper modelMapper;

	public VisitDTO getById(long id) {
		Visit result = visitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Visit with id: " + id+" has not been found"));
		return modelMapper.map(result, VisitDTO.class);
	}
	
	public Page<VisitDTO> findAll(Pageable pageable) {
		Page<Visit> result = visitRepository.findAll(pageable); 
		Page<VisitDTO> pageOfDTOs = result.map(mapToDto);
		return pageOfDTOs;
	}

	public VisitDTO addNew(Long vetId, Long patientId, Long epochInSeconds) throws NewVisitNotPossibleException, VetNotActiveException {
		// throws, if epoch is before now
		if(epochInSeconds - System.currentTimeMillis()/1000 < 0) {
			throw new NewVisitNotPossibleException("Creating new Visit failed: provided epoch time is not in the future.");
		}
		// throws, if Vet is busy at epoch
		throwIfVetBusyAt(epochInSeconds, vetId);

		// throws, if Patient is busy at epoch
		throwIfPatientBusyAt(epochInSeconds, patientId);
		
		// throws, if Vet is not active
		Vet vet = getVetOrThrowIfNotActive(vetId);
		
		// throws, if epochInSeconds is outside Vet's working days and working hours
		if(!isTimeOk(epochInSeconds, vet.getWorksFrom(), vet.getWorksTill(), vet.getWorkingDays())) {
			throw new NewVisitNotPossibleException("Creating new Visit failed: time is: outside working hours, outside of working days, or not at the top of the hour.");
		}
		// throws, if Vet does not have Patient's AnimalType
		Patient patient = getPatientOrThowIfAnimalTypeNotCompatibleWithVet(patientId, vet);
		
		Visit result = visitRepository.saveAndFlush(
				new Visit(
						vet, 
						patient, 
						epochInSeconds
						));
		return mapToDto.apply(result);
	}

	/**
	 * Returns Patient or throws, if Vet is not compatible with Patient's AnimalType
	 * @param patientId
	 * @param vet
	 * @return
	 * @throws NewVisitNotPossibleException
	 */
	private Patient getPatientOrThowIfAnimalTypeNotCompatibleWithVet(Long patientId, Vet vet) throws NewVisitNotPossibleException {
		Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new EntityNotFoundException("Patient with id " + patientId + " has not been found."));
		if(!vet.getAnimalTypes().contains(patient.getAnimalType())) {
			throw new NewVisitNotPossibleException("Doctor does not have animalType: " + patient.getAnimalType());
		}
		return patient;
	}

	private Vet getVetOrThrowIfNotActive(Long vetId) throws VetNotActiveException {
		Vet vet = vetRepository.findById(vetId).orElseThrow(() -> new EntityNotFoundException("Doctor with id " + vetId + " has not been found."));
		if(!vet.isActive()) {
			throw new VetNotActiveException("Creating Visit failed. Vet with id " + vetId + " is not active.");
		}
		return vet;
	}

	/*
	 * Checks, if Vet has any visits at epoch. If so, throws exception.
	 * Unconfirmed visits are also considered.
	 * TODO checking, if Vet is not on vacation
	 */
	private void throwIfVetBusyAt(long time, long vetId) throws NewVisitNotPossibleException {
		List<Visit> result = visitRepository.findByEpochAndVetId(time, vetId);
		if(result.size() >= 1) {
			String errorMessage = generateMessageWithArrayOfVisits("Vet", time, vetId, result);
			throw new NewVisitNotPossibleException(errorMessage);
		}
	}
	
	/*
	 * Checks, if Patient has any visits at epoch.
	 * Unconfirmed visits are also considered.
	 */
	private void throwIfPatientBusyAt(long time, long patientId) throws NewVisitNotPossibleException {
		List<Visit> result = visitRepository.findByEpochAndPatientId(time, patientId);
		if(result.size() >= 1) {
			String errorMessage = generateMessageWithArrayOfVisits("Patient", time, patientId, result);
			throw new NewVisitNotPossibleException(errorMessage);
		}
	}

	private String generateMessageWithArrayOfVisits(String field, long time, long id, List<Visit> result) {
		StringBuilder message = new StringBuilder(field + " with id " + id + "is busy at time " + time + ". ");
		message.append("Enrolled visits: [");
		result.forEach(visit -> message.append(visit.getId().toString() + ", "));
		// replaces last ", " to "]"
		message.replace(message.length()-2, message.length() -1, "]");
		return message.toString();
	}

	private Boolean isTimeOk(long epochInSeconds, int from, int till, List<DayOfWeek> workingDays) {
		Instant instant =  Instant.ofEpochSecond(epochInSeconds);
		ZonedDateTime zoned = instant.atZone(ZoneId.systemDefault());
		long defaultVisitTimeInSeconds = 60*60; // 1 hour
		
		// checks, if epoch is not at the top of the hour
		if(zoned.getSecond() != 0 || zoned.getMinute() != 0)
			return false;

		// checks, if epoch is outside working day
		if(!workingDays.contains(zoned.getDayOfWeek())) {
			return false;
		}
		
		// checks if epoch is outside working hours
		if(zoned.getHour()<from || zoned.getHour()>=till) {
			return false;
		}
		
		// checks, if visit will end after till (after the end of working day)
		long secondsToWorkingDayEnd = zoned.withHour(till).withMinute(0).withSecond(0).withNano(0).toEpochSecond() - zoned.toEpochSecond();
		if(secondsToWorkingDayEnd > 0 && secondsToWorkingDayEnd < defaultVisitTimeInSeconds) {
			return false;
		}
		return true;
	}
	
	private Function<Visit, VisitDTO> mapToDto = entity -> modelMapper.map(entity, VisitDTO.class);
}
