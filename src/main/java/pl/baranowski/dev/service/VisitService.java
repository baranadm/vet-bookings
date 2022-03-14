package pl.baranowski.dev.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.baranowski.dev.dto.DoctorsFreeSlotsDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.doctor.DoctorNotActiveException;
import pl.baranowski.dev.exception.epoch.InvalidEpochTimeException;
import pl.baranowski.dev.exception.visit.NewVisitNotPossibleException;
import pl.baranowski.dev.manager.Reception;
import pl.baranowski.dev.mapper.AvailableSlotsMapper;
import pl.baranowski.dev.mapper.VisitMapper;
import pl.baranowski.dev.model.AvailableSlotsFinder;
import pl.baranowski.dev.model.DoctorsFreeSlots;
import pl.baranowski.dev.model.EpochFutureTimeRange;
import pl.baranowski.dev.repository.VisitRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitService.class);

    private final VisitMapper visitMapper;
    private final AvailableSlotsMapper slotsMapper;
    private final VisitRepository visitRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public VisitService(VisitMapper visitMapper,
                        AvailableSlotsMapper slotsMapper,
                        VisitRepository visitRepository,
                        DoctorService doctorService,
                        PatientService patientService) {
        this.visitMapper = visitMapper;
        this.slotsMapper = slotsMapper;
        this.visitRepository = visitRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public VisitDTO getById(long id) throws NotFoundException {
        LOGGER.debug("getById(id='{}')", id);
        Visit result = findByIdOrThrow(id);
        LOGGER.debug("Result: {}", result);

        VisitDTO resultDTO = visitMapper.toDto(result);
        LOGGER.debug("Returning DTO: {}", resultDTO);
        return resultDTO;
    }

    private Visit findByIdOrThrow(long id) throws NotFoundException {
        return visitRepository.findById(id)
                              .orElseThrow(() -> {
                                  NotFoundException e = new NotFoundException("Visit with id: " + id + " has not been found");
                                  LOGGER.debug(e.getMessage(), e);
                                  return e;
                              });
    }

    public Page<VisitDTO> findAll(Pageable pageable) {
        LOGGER.debug("findAll(Pageable): {}", pageable);
        Page<Visit> result = visitRepository.findAll(pageable);
        LOGGER.debug("Received Page of {} Visits.", result.getSize());

        Page<VisitDTO> pageOfDTOs = result.map(visitMapper::toDto);
        LOGGER.debug("Returning Page of {} DTOs.", pageOfDTOs.getSize());
        return pageOfDTOs;
    }

    public VisitDTO addNew(Long doctorId,
                           Long patientId,
                           Long epochInSeconds) throws NewVisitNotPossibleException, NotFoundException, DoctorNotActiveException {
        LOGGER.info("addNew(doctorId='{}', patientId='{}', epochInSeconds='{}')",
                    doctorId,
                    patientId,
                    epochInSeconds);
        Reception reception = new Reception(doctorService, patientService);
        LOGGER.debug("Created new Reception: {}", reception);

        Visit possibleVisit = reception.createNewVisitIfPossible(doctorId, patientId, epochInSeconds);
        LOGGER.debug("New Visit is possible: {}", possibleVisit);

        Visit savedVisit = visitRepository.save(possibleVisit);
        LOGGER.debug("New Visit has been saved to database. Returning result: {}", savedVisit);
        return visitMapper.toDto(savedVisit);
    }

    public List<DoctorsFreeSlotsDTO> findAvailableVisits(String animalTypeName,
                                                         String medSpecialtyName,
                                                         Long epochStart,
                                                         Long epochEnd) throws InvalidEpochTimeException, NotFoundException {
        LOGGER.debug("findAvailableVisits(animalTypeName='{}', medSpecialtyName='{}', epochStart='{}', epochEnd='{}')", animalTypeName, medSpecialtyName, epochStart, epochEnd);

        List<Doctor> matchingDoctors = findDoctors(animalTypeName, medSpecialtyName);
        LOGGER.debug("Found {} matching doctors.", matchingDoctors.size());

        EpochFutureTimeRange timeRange = new EpochFutureTimeRange(epochStart, epochEnd);
        LOGGER.debug("Created TimeRange: {}", timeRange);

        AvailableSlotsFinder slotsFinder = new AvailableSlotsFinder(matchingDoctors, timeRange);
        LOGGER.debug("Created AvailableSlotsFinder: {}", slotsFinder);

        List<DoctorsFreeSlots> availableSlots = slotsFinder.find();
        LOGGER.debug("Found {} doctors with free slots.", availableSlots.size());

        List<DoctorsFreeSlotsDTO> availableSlotsDTO = availableSlots.stream().map(slotsMapper::toDto).collect(
                Collectors.toList());
        LOGGER.debug("Mapping results to {} DTOs.", availableSlotsDTO.size());
        return availableSlotsDTO;
    }

    private List<Doctor> findDoctors(String animalTypeName,
                                     String medSpecialtyName) throws NotFoundException {
        List<Doctor> doctorsWithSpecialities = findDoctorsWithSpecialities(animalTypeName, medSpecialtyName);
        List<Doctor> activeDoctorsWithSpecialties = chooseActiveDoctors(doctorsWithSpecialities);
        return activeDoctorsWithSpecialties;
    }

    private List<Doctor> chooseActiveDoctors(List<Doctor> doctorsWithSpecialities) {
        return doctorsWithSpecialities.stream().filter(Doctor::isActive).collect(Collectors.toList());
    }

    private List<Doctor> findDoctorsWithSpecialities(String animalTypeName,
                                                     String medSpecialtyName) throws NotFoundException {
        return doctorService.findByAnimalTypeNameAndMedSpecialtyName(animalTypeName, medSpecialtyName);
    }
}
