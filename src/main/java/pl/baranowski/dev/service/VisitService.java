package pl.baranowski.dev.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.AvailableSlotsAtTheDoctorDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.exception.InvalidEpochTimeException;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
import pl.baranowski.dev.manager.Reception;
import pl.baranowski.dev.mapper.VisitMapper;
import pl.baranowski.dev.model.AvailableSlotsAtTheDoctor;
import pl.baranowski.dev.model.AvailableSlotsFinder;
import pl.baranowski.dev.model.EpochFutureTimeRange;
import pl.baranowski.dev.repository.VisitRepository;

@Service
public class VisitService {

	private final VisitMapper mapper;
	private final VisitRepository visitRepository;
	private final DoctorService doctorService;
	private final PatientService patientService;

	public VisitService(VisitMapper mapper, VisitRepository visitRepository, DoctorService doctorService, PatientService patientService) {
		this.mapper = mapper;
		this.visitRepository = visitRepository;
		this.doctorService = doctorService;
		this.patientService = patientService;
	}

	public VisitDTO getById(long id) {
		Visit result = findByIdOrThrow(id);
		return mapper.toDto(result);
	}

	// TODO nie rzucać RuntimeException
	private Visit findByIdOrThrow(long id) {
		return visitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Visit with id: " + id+" has not been found"));
	}
	
	public Page<VisitDTO> findAll(Pageable pageable) {
		Page<Visit> result = visitRepository.findAll(pageable); 
		Page<VisitDTO> pageOfDTOs = result.map(mapper::toDto);
		return pageOfDTOs;
	}

	public VisitDTO addNew(Long doctorId, Long patientId, Long epochInSeconds) throws NewVisitNotPossibleException, DoctorNotActiveException {
		Reception reception = new Reception(doctorService, patientService);
		Visit possibleVisit = reception.createNewVisitIfPossible(doctorId, patientId, epochInSeconds);
		Visit savedVisit = visitRepository.save(possibleVisit);
		return mapper.toDto(savedVisit);
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
