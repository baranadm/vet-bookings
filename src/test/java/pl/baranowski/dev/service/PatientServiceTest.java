package pl.baranowski.dev.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.baranowski.dev.dto.NewPatientDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.patient.PatientAlreadyExistsException;
import pl.baranowski.dev.mapper.PatientMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.PatientRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PatientServiceTest {
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    AnimalTypeRepository animalTypeRepository;
    @Autowired
    PatientMapper mapper;
    @Autowired
    PatientService patientService;
    private AnimalType animalType;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();
        animalTypeRepository.deleteAll();
        animalType = animalTypeRepository.save(new AnimalType(1L, "Dog"));
        patient = patientRepository.save(new Patient("Rex", animalType, 12, "Juzio Kałuża", "kaluza@duza.pl"));
    }

    @Test
    void getById_whenEntityFound_returnsDTO() throws NotFoundException {
        PatientDTO expectedDTO = mapper.toDto(patient);
        PatientDTO resultDTO = patientService.getDto(patient.getId());
        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void getById_whenEntityNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> patientService.getDto(444L));
    }

    @Test
    void findAll_whenEntitiesExist_andPageableProvided_returnsDTOsPage() {
        patientRepository.save(new Patient("Hulk", animalType, 1, "Someone", "So@me.one"));
        patientRepository.save(new Patient("Piggy", animalType, 11, "Chuck", "no@rr.is"));
        List<Patient> patients = patientRepository.findAll();

        Pageable pageable = PageRequest.of(0, 2);
        Page<PatientDTO> expectedPage = new PageImpl<>(patients.subList(0, 2)
                                                               .stream()
                                                               .map(mapper::toDto)
                                                               .collect(Collectors.toList()),
                                                       pageable,
                                                       patients.size());

        Page<PatientDTO> resultPage = patientService.findAll(pageable);

        assertEquals(expectedPage.getPageable(), resultPage.getPageable());
        assertEquals(expectedPage.getContent(), resultPage.getContent());
    }

    @Test
    void findAll_whenEntitiesExist_andPageableNotProvided_returnsDTOsPage_withDefaultPageable() {
        patientRepository.save(new Patient("Hulk", animalType, 1, "Someone", "So@me.one"));
        patientRepository.save(new Patient("Piggy", animalType, 11, "Chuck", "no@rr.is"));
        List<Patient> patients = patientRepository.findAll();

        Pageable pageable = PatientService.DEFAULT_PAGEABLE;
        int fromElement = pageable.getPageNumber() * pageable.getPageSize();
        int toElement = fromElement + pageable.getPageSize();
        Page<PatientDTO> expectedPage = new PageImpl<>(patients.subList(fromElement,
                                                                        Math.min(toElement, patients.size()))
                                                               .stream()
                                                               .map(mapper::toDto)
                                                               .collect(Collectors.toList()),
                                                       pageable,
                                                       patients.size());

        Page<PatientDTO> resultPage = patientService.findAll();

        assertEquals(expectedPage.getPageable(), resultPage.getPageable());
        assertEquals(expectedPage.getContent(), resultPage.getContent());
    }

    @Test
    void findAll_whenEntitiesDoNotExist_returnsEmptyPage() {
        patientRepository.deleteAll();
        Pageable pageable = PageRequest.of(0, 2);
        Page<PatientDTO> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        Page<PatientDTO> resultPage = patientService.findAll(pageable);

        assertEquals(emptyPage.getPageable(), resultPage.getPageable());
        assertEquals(emptyPage.getContent(), resultPage.getContent());

    }

    @Test
    void addNew_whenAnimalTypeDoesNotExists_throwsNotFoundException() {
        NewPatientDTO newPatientDTO = new NewPatientDTO("Ron", "4", "Camel", "Harry P.", "harry.potter@hogwart.com");
        assertThrows(NotFoundException.class, () -> patientService.addNew(newPatientDTO));
    }

    @Test
    void addNew_whenAnimalTypeExistsAndPatientIsDuplicated_throwsPatientAlreadyExistsException() {
        NewPatientDTO newPatientDTO = new NewPatientDTO(patient.getName(),
                                                        patient.getAge().toString(),
                                                        patient.getAnimalType().getName(),
                                                        patient.getOwnerName(),
                                                        patient.getOwnerEmail());
        assertThrows(PatientAlreadyExistsException.class, () -> patientService.addNew(newPatientDTO));
    }

    @Test
    void addNew_whenNotDuplicatedAndAnimalTypeExists_correctlyCallsBusinessAndReturnsDTO() throws PatientAlreadyExistsException, NotFoundException {
        NewPatientDTO newPatientDTO = new NewPatientDTO("Ron",
                                                        "4",
                                                        animalType.getName(),
                                                        "Harry P.",
                                                        "harry.potter@hogwart.com");
        PatientDTO resultDTO = patientService.addNew(newPatientDTO);
        assertNotNull(resultDTO.getId());
        assertEquals(newPatientDTO.getName(), resultDTO.getName());
        assertEquals(newPatientDTO.getAge(), resultDTO.getAge().toString());
        assertEquals(newPatientDTO.getAnimalTypeName(), resultDTO.getAnimalType().getName());
        assertEquals(newPatientDTO.getOwnerName(), resultDTO.getOwnerName());
        assertEquals(newPatientDTO.getOwnerEmail(), resultDTO.getOwnerEmail());
    }
}