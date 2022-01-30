package pl.baranowski.dev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pl.baranowski.dev.dto.NewPatientDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.exception.PatientAllreadyExistsException;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.PatientRepository;

@SpringBootTest
class PatientServiceTest {

	@MockBean
	PatientRepository patientRepository;
	
	@MockBean
	AnimalTypeRepository animalTypeRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	PatientService patientService;
	
	Patient patient = new Patient("Rex", new AnimalType(1L, "Dog"), 12, "Juzio Kałuża", "kaluza@duza.pl");
	NewPatientDTO newPatient = new NewPatientDTO(patient.getName(), 
			patient.getAge().toString(), 
			patient.getAnimalType().getName(), 
			patient.getOwnerName(), 
			patient.getOwnerEmail());
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void getById() {
		//whenEntityExists - returnsDTO
		given(patientRepository.findById(patient.getId())).willReturn(Optional.of(patient));
		assertEquals(mapToDto.apply(patient), patientService.getById(patient.getId()));
		
		//whenEntityDoesNotExists - throwsEntityNotFoundException
		given(patientRepository.findById(patient.getId())).willReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> patientService.getById(patient.getId()));
	}
	
	@Test
	void findAll_whenValidInput() {
		//whenEntitiesExist - returnsDTOsPage
		Pageable pageable = PageRequest.of(2, 2);
		List<Patient> threePatients = Collections.nCopies(3, patient);
		Page<Patient> page = new PageImpl<>(threePatients, pageable, threePatients.size());
		given(patientRepository.findAll(pageable)).willReturn(page);
		Page<PatientDTO> expected = new PageImpl<>(
				threePatients.stream().map(mapToDto).collect(Collectors.toList()), 
				pageable, threePatients.size());
		
		assertEquals(expected, patientService.findAll(pageable));

		//whenEntitiesDoNotExist - returnsEmptyPage
		Page<Patient> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
		given(patientRepository.findAll(pageable)).willReturn(emptyPage);
		assertEquals(emptyPage.map(mapToDto), patientService.findAll(pageable));
	}
	
	@Test
	void findAll_whenInvalidInput_returns400andError() {
		
	}
	
	@Test
	void addNew_whenAnimalTypeDoesNotExists_throwsEntityNotFoundException() {
		given(animalTypeRepository.findById(patient.getAnimalType().getId())).willReturn(Optional.empty());
		
		assertThrows(EntityNotFoundException.class, () -> patientService.addNew(newPatient));
	}
	
	@Test
	void addNew_whenAnimalTypeExistsAndPatientIsDuplicated_throwsPatientAllreadyExistsException() {
		given(animalTypeRepository.findByName(patient.getAnimalType().getName())).willReturn(Collections.singletonList(patient.getAnimalType()));

		ExampleMatcher caseInsensitiveMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
		Example<Patient> patientExample = Example.of(patient, caseInsensitiveMatcher);
		given(patientRepository.findOne(patientExample)).willReturn(Optional.of(patient));

		assertThrows(PatientAllreadyExistsException.class, () -> patientService.addNew(newPatient));
	}
	
	@Test
	void addNew_whenNotDuplicatedAndAnimalTypeExists_correctlyCallsBusinessAndReturnsDTO() throws PatientAllreadyExistsException {
		// service will find appropriate AnimalType
		given(animalTypeRepository.findByName(patient.getAnimalType().getName())).willReturn(Collections.singletonList(patient.getAnimalType()));
		
		// service will not find doubled Patient
		ExampleMatcher caseInsensitiveMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
		Example<Patient> patientExample = Example.of(patient, caseInsensitiveMatcher);
		given(patientRepository.findOne(patientExample)).willReturn(Optional.empty());
		
		// repository will return new Patient with new id
		Patient expected = patient.withId(1L);
		given(patientRepository.saveAndFlush(patient)).willReturn(expected);
		assertEquals(mapToDto.apply(expected), patientService.addNew(newPatient));
	}
	
	private Function<Patient, PatientDTO> mapToDto = entity -> modelMapper.map(entity, PatientDTO.class);
}