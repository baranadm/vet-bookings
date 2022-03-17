package pl.baranowski.dev.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.doctor.DoctorAlreadyExistsException;
import pl.baranowski.dev.exception.doctor.DoctorDoubledSpecialtyException;
import pl.baranowski.dev.exception.doctor.DoctorNotActiveException;
import pl.baranowski.dev.mapper.DoctorMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.DoctorRepository;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;
    private final AnimalTypeRepository animalTypeRepository;
    private final MedSpecialtyRepository medSpecialtyRepository;
    private final DoctorMapper doctorMapper;

    public DoctorService(DoctorRepository doctorRepository,
                         AnimalTypeRepository animalTypeRepository,
                         MedSpecialtyRepository medSpecialtyRepository,
                         DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.animalTypeRepository = animalTypeRepository;
        this.medSpecialtyRepository = medSpecialtyRepository;
        this.doctorMapper = doctorMapper;
    }

    public DoctorDTO getDTO(long doctorId) throws NotFoundException {
        LOGGER.debug("get(doctorId='{}')", doctorId);

        Doctor doctor = getEntity(doctorId);
        LOGGER.debug("Found: {}", doctor);

        DoctorDTO result = doctorMapper.toDto(doctor);
        LOGGER.debug("Doctor returning mapped: {}", result);
        return result;
    }

    public Doctor getEntity(long doctorId) throws NotFoundException {
        LOGGER.debug("getEntity(doctorId='{}')", doctorId);
        Doctor doctor = doctorRepository.findById(doctorId)
                                        .orElseThrow(() -> {
                                            NotFoundException e = new NotFoundException("Doctor with id=" + doctorId + " has not been found.");
                                            LOGGER.error(e.getMessage(), e);
                                            return e;
                                        });
        LOGGER.debug("Returning found doctor: {}", doctor);
        return doctor;
    }

    protected List<Doctor> findByAnimalTypeNameAndMedSpecialtyName(String animalTypeName,
                                                                   String medSpecialtyName) throws NotFoundException {
        LOGGER.debug("findByAnimalTypeNameAndMedSpecialtyName(animalTypeName='{}', medSpecialtyName='{}')",
                     animalTypeName,
                     medSpecialtyName);

        AnimalType animalType = findAnimalType(animalTypeName);
        LOGGER.debug("AnimalType received: {}", animalType);

        MedSpecialty medSpecialty = findMedSpecialty(medSpecialtyName);
        LOGGER.debug("MedSpecialty received: {}", medSpecialty);

        List<Doctor> result = doctorRepository.findByAnimalTypesAndMedSpecialties(animalType, medSpecialty);
        LOGGER.debug("Returning Doctor list with size: {}", result.size());
        return result;
    }

    private AnimalType findAnimalType(String animalTypeName) throws NotFoundException {
        Optional<AnimalType> result = animalTypeRepository.findOneByName(animalTypeName);
        return result.orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Animal type with name '" + animalTypeName + "'has not been found.");
            LOGGER.error(e.getMessage(), e);
            return e;
        });
    }

    private MedSpecialty findMedSpecialty(String medSpecialtyName) throws NotFoundException {
        Optional<MedSpecialty> result = medSpecialtyRepository.findOneByName(medSpecialtyName);
        return result.orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Med specialty with name '" + medSpecialtyName + "' has not been found.");
            LOGGER.error(e.getMessage(), e);
            return e;
        });
    }

    public Page<DoctorDTO> findAll(Pageable pageable) {
        LOGGER.debug("findAll(pageable='{}')", pageable);
        Page<Doctor> doctors = doctorRepository.findAll(pageable);
        LOGGER.debug("Found '{}' doctors.", doctors.getSize());

        Page<DoctorDTO> doctorsDTO = new PageImpl<>(
                doctors.toList().stream()
                       .map(doctorMapper::toDto)
                       .collect(Collectors.toList()),
                doctors.getPageable(),
                doctors.getSize());
        LOGGER.debug("Returning Page with mapped DoctorDTOs. Size: '{}'", doctorsDTO.getSize());
        return doctorsDTO;
    }

    public DoctorDTO addNew(DoctorDTO doctorDTO) throws DoctorAlreadyExistsException {
        LOGGER.debug("addNew(doctorDTO): {}", doctorDTO);
        if (!doctorRepository.findByNip(doctorDTO.getNip()).isEmpty()) {
            DoctorAlreadyExistsException e = new DoctorAlreadyExistsException(doctorDTO.getNip());
            LOGGER.debug(e.getMessage(), e);
            throw e;
        }
        Doctor doctor = doctorMapper.toEntity(doctorDTO);
        LOGGER.debug("Mapped DoctorDTO to Doctor: {}", doctor);

        Doctor result = doctorRepository.saveAndFlush(doctor);
        LOGGER.debug("Saved new Doctor, result: {}", result);

        DoctorDTO resultDTO = doctorMapper.toDto(result);
        LOGGER.debug("Returning result mapped to DTO: {}", resultDTO);
        return resultDTO;
    }

    public DoctorDTO fire(Long id) throws DoctorNotActiveException, NotFoundException {
        LOGGER.debug("fire(id='{}')", id);

        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        LOGGER.debug("Received Optional of Doctor: {}", doctorOpt);

        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            LOGGER.debug("Doctor has been found: {}", doctor);
            if (doctor.getActive()) { // if Doctor is active, sets active to false
                doctor.setActive(false);
                LOGGER.debug("Doctor has been set to inactive: {}", doctor);

                DoctorDTO firedDoctor = doctorMapper.toDto(doctorRepository.save(doctor));
                LOGGER.debug("Returning fired Doctor result: {}", firedDoctor);
                return firedDoctor;
            } else { // if Doctor is inactive, throws exception
                DoctorNotActiveException e = new DoctorNotActiveException(doctor.getId());
                LOGGER.error(e.getMessage(), e);
                throw e;
            }
        } else {
            NotFoundException e = new NotFoundException("Doctor has not ben found");
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    public DoctorDTO addAnimalType(Long doctorId,
                                   Long animalTypeId) throws NotFoundException, DoctorNotActiveException, DoctorDoubledSpecialtyException {
        LOGGER.debug("addAnimalType(doctorId='{}', animalTypeId='{}')", doctorId, animalTypeId);

        Doctor doctor = getEntity(doctorId);
        LOGGER.debug("Found Doctor: {}", doctor);

        if (!doctor.getActive()) {
            DoctorNotActiveException e = new DoctorNotActiveException(doctor.getId());
            LOGGER.error(e.getMessage(), e);
            throw e;
        }

        AnimalType animalType = animalTypeRepository.findById(animalTypeId)
                                                    .orElseThrow(() -> {
                                                        NotFoundException e = new NotFoundException(
                                                                "animal type with id: " + animalTypeId + " has not been found");
                                                        LOGGER.error(e.getMessage(), e);
                                                        return e;
                                                    });
        LOGGER.debug("Found AnimalType: {}", animalType);

        if (doctor.getAnimalTypes().contains(animalType)) {
            DoctorDoubledSpecialtyException e = new DoctorDoubledSpecialtyException("Animal Type");
            LOGGER.error(e.getMessage(), e);
            throw e;
        }

        doctor.addAnimalType(animalType);
        LOGGER.debug("Animal Type has been added to Doctor. Doctor after changes: {}", doctor);

        DoctorDTO resultDTO = doctorMapper.toDto(doctorRepository.saveAndFlush(doctor));
        LOGGER.debug("Returning Doctor DTO: {}", resultDTO);
        return resultDTO;
    }

    public DoctorDTO addMedSpecialty(Long doctorId,
                                     Long msId) throws NotFoundException, DoctorNotActiveException, DoctorDoubledSpecialtyException {
        LOGGER.debug("addMedSpecialty(doctorId='{}', msId='{}')", doctorId, msId);

        Doctor doctor = getEntity(doctorId);
        LOGGER.debug("Found Doctor: {}", doctor);
        if (!doctor.getActive()) {
            DoctorNotActiveException e = new DoctorNotActiveException(doctor.getId());
            LOGGER.error(e.getMessage(), e);
            throw e;
        }

        MedSpecialty medSpecialty = medSpecialtyRepository.findById(msId)
                                                          .orElseThrow(() -> {
                                                              NotFoundException e = new NotFoundException(
                                                                      "Medical specialty with id " + msId + " has not been found.");
                                                              LOGGER.error(e.getMessage(), e);
                                                              return e;
                                                          });

        if (doctor.getMedSpecialties().contains(medSpecialty)) {
            DoctorDoubledSpecialtyException e = new DoctorDoubledSpecialtyException("Medical Specialty");
            LOGGER.error(e.getMessage(), e);
            throw e;
        }

        doctor.addMedSpecialty(medSpecialty);

        DoctorDTO resultDTO = doctorMapper.toDto(doctorRepository.saveAndFlush(doctor));
        LOGGER.debug("MedSpecialty has been added to Doctor. Returning result: {}", resultDTO);
        return resultDTO;
    }
}
