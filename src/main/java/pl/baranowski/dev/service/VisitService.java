package pl.baranowski.dev.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.SingleCheckResultDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
import pl.baranowski.dev.exception.SearchRequestInvalidException;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.mapper.CustomMapper;
import pl.baranowski.dev.repository.PatientRepository;
import pl.baranowski.dev.repository.DoctorRepository;
import pl.baranowski.dev.repository.VisitRepository;

@Service
public class VisitService {

	@Autowired
	VisitRepository visitRepository;
	@Autowired
	PatientRepository patientRepository;
	@Autowired
	DoctorRepository doctorRepository;
	@Autowired
	CustomMapper mapper;
	@Autowired
	DoctorService doctorService;

	public VisitDTO getById(long id) {
		Visit result = visitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Visit with id: " + id+" has not been found"));
		return mapper.toDto(result);
	}
	
	public Page<VisitDTO> findAll(Pageable pageable) {
		Page<Visit> result = visitRepository.findAll(pageable); 
		Page<VisitDTO> pageOfDTOs = result.map(mapper::toDto);
		return pageOfDTOs;
	}

	public VisitDTO addNew(Long doctorId, Long patientId, Long epochInSeconds) throws NewVisitNotPossibleException, DoctorNotActiveException {
		// throws, if epoch is before now
		if(epochInSeconds - System.currentTimeMillis()/1000 < 0) {
			throw new NewVisitNotPossibleException("Creating new Visit failed: provided epoch time is not in the future.");
		}
		// throws, if Doctor is busy at epoch
		throwIfDoctorBusyAt(epochInSeconds, doctorId);

		// throws, if Patient is busy at epoch
		throwIfPatientBusyAt(epochInSeconds, patientId);
		
		// throws, if Doctor is not active
		Doctor doctor = getDoctorOrThrowIfNotActive(doctorId);
		
		/*
		 *  throws, if:
		 *  - epochInSeconds is outside Doctor's working days 
		 *  - epochInSeconds is outside Doctor's working hours
		 *  - epochInSeconds is not at the top of the hour (e.g. 10:00:01 is not correct)
		 */
		if(!isTimeOk(epochInSeconds, doctor.getWorksFrom(), doctor.getWorksTill(), doctor.getWorkingDays())) {
			throw new NewVisitNotPossibleException("Creating new Visit failed: time is: outside working hours, outside of working days, or not at the top of the hour.");
		}

		// throws, if Doctor does not have Patient's AnimalType
		Patient patient = getPatientOrThowIfAnimalTypeNotCompatibleWithDoctor(patientId, doctor);
		
		Visit result = visitRepository.saveAndFlush(
				new Visit.VisitBuilder(doctor, patient, epochInSeconds).build());
		return mapper.toDto(result);
	}
	
	// TODO tests...
	public List<SingleCheckResultDTO> findFreeSlots(String animalTypeName, String medSpecialtyName, String epochStart, String epochEnd, String intervalStr) throws SearchRequestInvalidException {
		// decodes validated epoch start
		long start = Long.decode(epochStart);
		// decodes validated epoch end
		long end = Long.decode(epochEnd);
		// decodes validated interval
		long interval = Long.decode(intervalStr);
		
		// finds Doctors with matching AnimalType and MedSpecialty
		List<Doctor> matchingDoctors = doctorService.findByAnimalTypeNameAndMedSpecialtyName(animalTypeName, medSpecialtyName);

		// creates list of SingleCheckResultDTO and populates it with Doctors and their free times
		List<SingleCheckResultDTO> result = new ArrayList<>();
		for(Doctor doctor: matchingDoctors) {
			result.add(new SingleCheckResultDTO(mapper.toDto(doctor), findFreeSlotsForDoctor(doctor, start, end, interval)));
		}
			
		return result;
	}
	
	public List<Long> findFreeSlotsForDoctor(Doctor doctor, Long epochStart, Long epochEnd, Long interval) throws SearchRequestInvalidException {
		// validate epochs' values
		if(epochStart >= epochEnd) {
			throw new SearchRequestInvalidException("Searching request not valid: epoch start should be less than epoch end.");
		}
		if(epochStart < System.currentTimeMillis()/1000) {
			throw new SearchRequestInvalidException("Searching request not valid: epoch start should be later than now.");
		}
		
		// generates times list (e.g. 9:00, 10:00, 11:00) between epochStart (9:00) and End (11:00) with interval (1 hour = 3600s)
		List<Long> topHours = createTimeSlots(epochStart, epochEnd, interval);
		
		// reduces times list by taken (busy) slots
		List<Long> result = topHours.stream()
				.filter(hour -> !doctor.isBusyAt(hour, interval))
				.collect(Collectors.toList());
		
		return result;
	}
	
	
	/**
	 * Generates times list from epochStart (inclusive) to epochEnd (inclusive) with interval.
	 * Example:
	 * epochStart = 1894006800 (GMT: Monday, 7 January 2030 09:00:00),
	 * epochEnd = 1894014000 (GMT: Monday, 7 January 2030 09:00:00),
	 * interval = 900 (15 min),
	 * Result: 1894006800, 1894007700, 1894008600, 1894009500, 1894010400, 1894011300, 1894012200, 1894013100, 1894014000
	 * @param epochStart inclusive, seconds
	 * @param epochEnd inclusive, seconds
	 * @param interval seconds
	 * @return epoch time list, seconds
	 */
	public List<Long> createTimeSlots(long epochStart, long epochEnd, long interval) {
		List<Long> result = new ArrayList<>();
		/*
		 *  epochStart % one hour (3600s) = minutes from last top hour (remainder)
		 *  epochStart - remainder + one hour = first top hour after epochStart
		 */
		long remainder = (epochStart % interval);
		long top = epochStart;
		top += remainder > 0 ? -remainder + interval : 0; // if remainder 0, subtract reminder and add 1 hour
		for(; top<=epochEnd; top+=interval) { // epochEnd inclusive
			result.add(top);
		}
		System.out.println(result);
		return result;
	}

	/**
	 * Returns Patient or throws, if Doctor is not compatible with Patient's AnimalType
	 * @param patientId
	 * @param doctor
	 * @return
	 * @throws NewVisitNotPossibleException
	 */
	private Patient getPatientOrThowIfAnimalTypeNotCompatibleWithDoctor(Long patientId, Doctor doctor) throws NewVisitNotPossibleException {
		Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new EntityNotFoundException("Patient with id " + patientId + " has not been found."));
		if(!doctor.getAnimalTypes().contains(patient.getAnimalType())) {
			throw new NewVisitNotPossibleException("Doctor does not have animalType: " + patient.getAnimalType());
		}
		return patient;
	}

	private Doctor getDoctorOrThrowIfNotActive(Long doctorId) throws DoctorNotActiveException {
		Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFoundException("Doctor with id " + doctorId + " has not been found."));
		if(!doctor.isActive()) {
			throw new DoctorNotActiveException("Creating Visit failed. Doctor with id " + doctorId + " is not active.");
		}
		return doctor;
	}

	/*
	 * Checks, if Doctor has any visits at epoch. If so, throws exception.
	 * Unconfirmed visits are also considered.
	 * TODO checking, if Doctor is not on vacation
	 */
	private void throwIfDoctorBusyAt(long time, long doctorId) throws NewVisitNotPossibleException {
		List<Visit> result = visitRepository.findByEpochAndDoctorId(time, doctorId);
		if(result.size() >= 1) {
			String errorMessage = generateMessageWithArrayOfVisits("Doctor", time, doctorId, result);
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
	
}
