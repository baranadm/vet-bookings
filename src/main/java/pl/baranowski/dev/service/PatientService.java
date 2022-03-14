package pl.baranowski.dev.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import pl.baranowski.dev.dto.NewPatientDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.patient.PatientAlreadyExistsException;
import pl.baranowski.dev.mapper.PatientMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.PatientRepository;

import java.util.Optional;

@Service
public class PatientService {
    public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepo;
    private final AnimalTypeRepository animalTypeRepo;
    private final PatientMapper mapper;

    public PatientService(PatientRepository patientRepo,
                          AnimalTypeRepository animalTypeRepo,
                          PatientMapper mapper) {
        this.patientRepo = patientRepo;
        this.animalTypeRepo = animalTypeRepo;
        this.mapper = mapper;
    }

    public Page<PatientDTO> findAll() {
        LOGGER.debug("findAll() (default pageable)");
        Page<PatientDTO> result = findAll(DEFAULT_PAGEABLE);
        LOGGER.debug("Returning result Page with {} elements.", result.getSize());
        return result;
    }

    public Page<PatientDTO> findAll(Pageable pageable) {
        LOGGER.debug("findAll(pageable): {}", pageable);

        Page<Patient> result = patientRepo.findAll(pageable);
        LOGGER.debug("Found {} patients.", result.getSize());

        Page<PatientDTO> resultDTO = result.map(mapper::toDto);
        LOGGER.debug("Returning Page of {} DTOs.", resultDTO.getSize());
        return resultDTO;
    }

    public PatientDTO getDto(Long patientId) throws NotFoundException {
        LOGGER.debug("getDto(patientId='{}')", patientId);

        Patient result = getEntity(patientId);
        LOGGER.debug("Found: {}", result);

        PatientDTO resultDTO = mapper.toDto(result);
        LOGGER.debug("Returning DTO result: {}", resultDTO);
        return resultDTO;
    }

    public Patient getEntity(Long patientId) throws NotFoundException {
        LOGGER.debug("getEntity(patientId='{}')", patientId);

        Patient result = patientRepo.findById(patientId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Patient with id " + patientId + " has not been found.");
            LOGGER.error(e.getMessage(), e);
            return e;
        });

        LOGGER.debug("Returning found Patient: {}", result);
        return result;
    }

    public PatientDTO addNew(NewPatientDTO newPatientDTO) throws PatientAlreadyExistsException, NotFoundException {
        LOGGER.info("addNew(newPatientDTO): {}", newPatientDTO);

        AnimalType animalType = findAnimalType(newPatientDTO.getAnimalTypeName());
        LOGGER.debug("Requested Animal Type found: {}", animalType);

        Patient patient = new Patient(newPatientDTO.getName(),
                                      animalType,
                                      Integer.valueOf(newPatientDTO.getAge()), // value validated by @Valid @RequestBody
                                      newPatientDTO.getOwnerName(), newPatientDTO.getOwnerEmail());
        LOGGER.debug("Created new Patient body from newPatientDTO: {}", patient);

        ExampleMatcher caseInsensitiveMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
        Example<Patient> patientExample = Example.of(patient, caseInsensitiveMatcher);
        Optional<Patient> old = patientRepo.findOne(patientExample);
        if (old.isPresent()) {
            PatientAlreadyExistsException e = new PatientAlreadyExistsException(
                    newPatientDTO);
            LOGGER.error(e.getMessage(), e);
            throw e;
        }

        Patient result = patientRepo.saveAndFlush(patient);
        LOGGER.debug("New Patient saved to database: {}", result);

        PatientDTO resultDTO = mapper.toDto(result);
        LOGGER.debug("Returning Patient DTO: {}", resultDTO);
        return resultDTO;
    }

    private AnimalType findAnimalType(String animalTypeName) throws NotFoundException {
        Optional<AnimalType> result = animalTypeRepo.findOneByName(animalTypeName);
        return result.orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Animal type with name '" + animalTypeName + "' has not been found.");
            LOGGER.error(e.getMessage(), e);
            return e;
        });
    }

}