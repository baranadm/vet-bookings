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
        Visit result = findByIdOrThrow(id);
        return visitMapper.toDto(result);
    }

    private Visit findByIdOrThrow(long id) throws NotFoundException {
        return visitRepository.findById(id)
                              .orElseThrow(() -> new NotFoundException("Visit with id: " + id + " has not been found"));
    }

    public Page<VisitDTO> findAll(Pageable pageable) {
        Page<Visit> result = visitRepository.findAll(pageable);
        Page<VisitDTO> pageOfDTOs = result.map(visitMapper::toDto);
        return pageOfDTOs;
    }

    public VisitDTO addNew(Long doctorId,
                           Long patientId,
                           Long epochInSeconds) throws NewVisitNotPossibleException, NotFoundException, DoctorNotActiveException {
        LOGGER.info("Received addNew() request with params: doctorId='{}', patientId='{}', epochInSeconds='{}'",
                    doctorId,
                    patientId,
                    epochInSeconds);
        Reception reception = new Reception(doctorService, patientService);
        Visit possibleVisit = reception.createNewVisitIfPossible(doctorId, patientId, epochInSeconds);
        Visit savedVisit = visitRepository.save(possibleVisit);
        return visitMapper.toDto(savedVisit);
    }

    public List<DoctorsFreeSlotsDTO> findAvailableVisits(String animalTypeName,
                                                         String medSpecialtyName,
                                                         Long epochStart,
                                                         Long epochEnd) throws InvalidEpochTimeException, NotFoundException {
        List<Doctor> matchingDoctors = findDoctors(animalTypeName, medSpecialtyName);

        EpochFutureTimeRange timeRange = new EpochFutureTimeRange(epochStart, epochEnd);
        AvailableSlotsFinder slotsFinder = new AvailableSlotsFinder(matchingDoctors, timeRange);

        List<DoctorsFreeSlots> availableSlots = slotsFinder.find();
        List<DoctorsFreeSlotsDTO> availableSlotsDTO = availableSlots.stream().map(slotsMapper::toDto).collect(
                Collectors.toList());
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
