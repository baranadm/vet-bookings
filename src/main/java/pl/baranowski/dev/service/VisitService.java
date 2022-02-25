package pl.baranowski.dev.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.AvailableSlotsAtTheDoctorDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.exception.InvalidEpochTimeException;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
import pl.baranowski.dev.manager.VisitsManager;
import pl.baranowski.dev.mapper.CustomMapper;
import pl.baranowski.dev.model.AvailableSlotsAtTheDoctor;
import pl.baranowski.dev.model.AvailableSlotsFinder;
import pl.baranowski.dev.model.EpochFutureTimeRange;
import pl.baranowski.dev.repository.DoctorRepository;
import pl.baranowski.dev.repository.PatientRepository;
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
	@Autowired
	PatientService patientService;
	@Autowired
	VisitsManager visitsManager;

	public VisitDTO getById(long id) {
		Visit result = findByIdOrThrow(id);
		return mapper.toDto(result);
	}

	private Visit findByIdOrThrow(long id) {
		return visitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Visit with id: " + id+" has not been found"));
	}
	
	public Page<VisitDTO> findAll(Pageable pageable) {
		Page<Visit> result = visitRepository.findAll(pageable); 
		Page<VisitDTO> pageOfDTOs = result.map(mapper::toDto);
		return pageOfDTOs;
	}

	public VisitDTO addNew(Long doctorId, Long patientId, Long epochInSeconds) throws NewVisitNotPossibleException, DoctorNotActiveException {
		Doctor doctor = doctorService.get(doctorId);
		Patient patient = patientService.get(patientId);

		/*----------------------
		 * czy jest sens tworzyć visitsManager w celu walidacji wizyty i dopisywania wizyt do doktorów/pacjentów?
		 * czy nie lepiej zrobić to po prostu w visitsService
		 * 
		 * czy w ogóle trzeba ręcznie dopisywać wizyty do doktórów i pacjentów? i tak JPA zapisuje to w bazie danych.
		 * 
		 * jak napisać tę funkcjonalność tak, aby było łatwo robić testy
		 */
		Visit newVisit = visitsManager.createNewVisit(doctor, patient, epochInSeconds);
		Visit newVisitDto = visitRepository.saveAndFlush(newVisit);
		return mapper.toDto(newVisitDto);
	}

	private Visit buildNewVisit(Long epochInSeconds, Doctor doctor, Patient patient) throws NewVisitNotPossibleException, DoctorNotActiveException {
		return new Visit.VisitBuilder(doctor, patient, epochInSeconds).build();
	}
	
	public List<AvailableSlotsAtTheDoctorDTO> findAvailableSlotsAtTheDoctorsWithParams(String animalTypeName, String medSpecialtyName, String epochStart, String epochEnd) throws InvalidEpochTimeException {
		List<Doctor> matchingDoctors = findDoctorsWithSpecialities(animalTypeName, medSpecialtyName);
		EpochFutureTimeRange timeRange = EpochFutureTimeRange.fromStrings(epochStart, epochEnd);		
		AvailableSlotsFinder slotsFinder = new AvailableSlotsFinder(matchingDoctors, timeRange);
		List<AvailableSlotsAtTheDoctor> availableSlots = slotsFinder.find();
		
		List<AvailableSlotsAtTheDoctorDTO> availableSlotsDTO = availableSlots.stream().map(mapper::toDto).collect(Collectors.toList());
		return availableSlotsDTO;
	}

	private List<Doctor> findDoctorsWithSpecialities(String animalTypeName, String medSpecialtyName) {
		return doctorService.findByAnimalTypeNameAndMedSpecialtyName(animalTypeName, medSpecialtyName);
	}
	
}
