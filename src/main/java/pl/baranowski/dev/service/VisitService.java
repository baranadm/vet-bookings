package pl.baranowski.dev.service;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import com.sun.jdi.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.AvailableSlotsAtTheDoctorDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.epoch.InvalidEpochTimeException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.manager.Reception;
import pl.baranowski.dev.mapper.VisitMapper;
import pl.baranowski.dev.model.AvailableSlotsAtTheDoctor;
import pl.baranowski.dev.model.AvailableSlotsFinder;
import pl.baranowski.dev.model.EpochFutureTimeRange;
import pl.baranowski.dev.repository.VisitRepository;

@Service
public class VisitService {
	private static final Logger LOGGER = LoggerFactory.getLogger(VisitService.class);

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

	public VisitDTO getById(long id) throws NotFoundException {
		Visit result = findByIdOrThrow(id);
		return mapper.toDto(result);
	}

	private Visit findByIdOrThrow(long id) throws NotFoundException {
		return visitRepository.findById(id).orElseThrow(() -> new NotFoundException("Visit with id: " + id+" has not been found"));
	}
	
	public Page<VisitDTO> findAll(Pageable pageable) {
		Page<Visit> result = visitRepository.findAll(pageable); 
		Page<VisitDTO> pageOfDTOs = result.map(mapper::toDto);
		return pageOfDTOs;
	}

	public VisitDTO addNew(Long doctorId, Long patientId, Long epochInSeconds) throws Exception {
		LOGGER.info("Received addNew() request with params: doctorId='{}', patientId='{}', epochInSeconds='{}'", doctorId, patientId, epochInSeconds);
		Reception reception = new Reception(doctorService, patientService);
		Visit possibleVisit = reception.createNewVisitIfPossible(doctorId, patientId, epochInSeconds);
		Visit savedVisit = visitRepository.save(possibleVisit);
		return mapper.toDto(savedVisit);
	}

	public List<AvailableSlotsAtTheDoctorDTO> findAvailableSlotsAtTheDoctorsWithParams(String animalTypeName, String medSpecialtyName, String epochStart, String epochEnd) throws InvalidEpochTimeException, NotFoundException {
		List<Doctor> matchingDoctors = findDoctorsWithSpecialities(animalTypeName, medSpecialtyName);
		EpochFutureTimeRange timeRange = EpochFutureTimeRange.fromStrings(epochStart, epochEnd);		
		AvailableSlotsFinder slotsFinder = new AvailableSlotsFinder(matchingDoctors, timeRange);
		List<AvailableSlotsAtTheDoctor> availableSlots = slotsFinder.find();
		
//		List<AvailableSlotsAtTheDoctorDTO> availableSlotsDTO = availableSlots.stream().map(mapper::toDto).collect(Collectors.toList());
		List<AvailableSlotsAtTheDoctorDTO> availableSlotsDTO = Collections.emptyList();
		return availableSlotsDTO;
	}

	private List<Doctor> findDoctorsWithSpecialities(String animalTypeName, String medSpecialtyName) throws NotFoundException {
		return doctorService.findByAnimalTypeNameAndMedSpecialtyName(animalTypeName, medSpecialtyName);
	}
	
}
