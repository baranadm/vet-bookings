package pl.baranowski.dev.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class PatientServiceTest {

    @MockBean
    PatientRepository patientRepository;

    @MockBean
    AnimalTypeRepository animalTypeRepository;

    @Autowired
    PatientMapper mapper;

    @Autowired
    PatientService patientService;

    Patient patient = new Patient("Rex", new AnimalType(1L, "Dog"), 12, "Juzio Kałuża", "kaluza@duza.pl");
    NewPatientDTO newPatient = new NewPatientDTO(patient.getName(),
                                                 patient.getAge().toString(),
                                                 patient.getAnimalType().getName(),
                                                 patient.getOwnerName(),
                                                 patient.getOwnerEmail());

    @BeforeEach
    void setUp() {
    }

    @Test
    void getById_whenEntityFound_returnsDTO() throws NotFoundException {
        given(patientRepository.findById(patient.getId())).willReturn(Optional.of(patient));
        assertEquals(mapper.toDto(patient), patientService.getDto(patient.getId()));
    }

    @Test
    void getById_whenEntityNotFound_throwsNotFoundException() {
        given(patientRepository.findById(patient.getId())).willReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> patientService.getDto(patient.getId()));
    }

    @Test
    void findAll_returnsDTOsPage() {
        //whenEntitiesExist - returnsDTOsPage
        Pageable pageable = PageRequest.of(2, 2);
        List<Patient> threePatients = Collections.nCopies(3, patient);
        Page<Patient> page = new PageImpl<>(threePatients, pageable, threePatients.size());
        given(patientRepository.findAll(pageable)).willReturn(page);
        Page<PatientDTO> expected = new PageImpl<>(
                threePatients.stream().map(mapper::toDto).collect(Collectors.toList()),
                pageable, threePatients.size());

        assertEquals(expected, patientService.findAll(pageable));

        //whenEntitiesDoNotExist - returnsEmptyPage
        Page<Patient> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        given(patientRepository.findAll(pageable)).willReturn(emptyPage);
        assertEquals(emptyPage.map(mapper::toDto), patientService.findAll(pageable));
    }

    @Test
    void addNew_whenAnimalTypeDoesNotExists_throwsNotFoundException() {
        given(animalTypeRepository.findById(patient.getAnimalType().getId())).willReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> patientService.addNew(newPatient));
    }

    @Test
    void addNew_whenAnimalTypeExistsAndPatientIsDuplicated_throwsPatientAlreadyExistsException() {
        given(animalTypeRepository.findOneByName(patient.getAnimalType()
                                                         .getName())).willReturn(Optional.of(patient.getAnimalType()));

        ExampleMatcher caseInsensitiveMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
        Example<Patient> patientExample = Example.of(patient, caseInsensitiveMatcher);
        given(patientRepository.findOne(patientExample)).willReturn(Optional.of(patient));

        assertThrows(PatientAlreadyExistsException.class, () -> patientService.addNew(newPatient));
    }

    @Test
    void addNew_whenNotDuplicatedAndAnimalTypeExists_correctlyCallsBusinessAndReturnsDTO() throws PatientAlreadyExistsException, NotFoundException {
        // service will find appropriate AnimalType
        given(animalTypeRepository.findOneByName(patient.getAnimalType()
                                                         .getName())).willReturn(Optional.of(patient.getAnimalType()));

        // service will not find doubled Patient
        ExampleMatcher caseInsensitiveMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
        Example<Patient> patientExample = Example.of(patient, caseInsensitiveMatcher);
        given(patientRepository.findOne(patientExample)).willReturn(Optional.empty());

        // repository will return new Patient with new id
        Patient expected = patient.withId(1L);
        given(patientRepository.saveAndFlush(patient)).willReturn(expected);
        assertEquals(mapper.toDto(expected), patientService.addNew(newPatient));
    }

}