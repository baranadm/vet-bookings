package pl.baranowski.dev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
import pl.baranowski.dev.repository.PatientRepository;
import pl.baranowski.dev.repository.VetRepository;
import pl.baranowski.dev.repository.VisitRepository;


// TODO inspect, that in datebase, New Visit's isConfirmed = null
@SpringBootTest
class VisitServiceTest {

	@Autowired
	VisitService visitService;
	
	@Autowired
	ModelMapper modelMapper;

	@MockBean
	VisitRepository visitRepository;
	@MockBean
	VetRepository vetRepository;
	@MockBean
	PatientRepository patientRepository;

	// Below I present to You our todays Heroes:
	long oneWeekFromNow;
	AnimalType animalType = new AnimalType(1L, "Owad");
	MedSpecialty medSpecialty = new MedSpecialty(2L, "Czółkolog");
	Vet vet = new Vet(3L, "Kazik", "Montana", new BigDecimal(220), "1111111111");
	Patient patient = new Patient(4L, "Karaluch", animalType, 13, "Lubiacz Owadów", "ijegomail@sld.pl");
	Visit visit = new Visit(13L, vet, patient, oneWeekFromNow);
	
	@BeforeEach
	void setUp() throws Exception {
		// setting epoch to be one week later in the future
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 7);
		this.oneWeekFromNow = cal.getTimeInMillis();
	}

	@Test
	void getById_correctCallToRepositoryAndDTOReturnValue() {
		Long id = 13L;
		given(visitRepository.findById(id)).willReturn(Optional.of(visit));

		VisitDTO expected = modelMapper.map(visit, VisitDTO.class);
		VisitDTO result = visitService.getById(id);
		
		ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
		verify(visitRepository, times(1)).findById(idCaptor.capture());
		
		// verifies correct repo call
		assertEquals(id, idCaptor.getValue());
		
		// verifies repo result mapping
		assertEquals(expected, result);
	}
	
	@Test
	void getById_whenNoEntity_throwsEntityNotFoundException() {
		long id = 100L;
		given(visitRepository.findById(id)).willReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> visitService.getById(id));
	}
	
	@Test
	void findAll_correctCallToRepositoryAndPageOfDTOsReturnValue() {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Visit> mockedRepoResult = new PageImpl<Visit>(Collections.nCopies(3, visit), pageable, 3);
		given(visitRepository.findAll(pageable)).willReturn(mockedRepoResult);
		
		// verifies return value
		Page<VisitDTO> expected = mockedRepoResult.map(visit -> modelMapper.map(visit, VisitDTO.class));
		Page<VisitDTO> result = visitService.findAll(pageable);
		assertEquals(expected, result);
		
		// verifies repo call
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(visitRepository, times(1)).findAll(pageableCaptor.capture());
		assertEquals(pageable, pageableCaptor.getValue());
	}
	
	@Test
	void findAll_whenNoEntities_emptyPageReturnValue() {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Visit> emptyPage = new PageImpl<Visit>(Collections.emptyList());

		given(visitRepository.findAll(pageable)).willReturn(emptyPage);

		Page<VisitDTO> emptyDTOPage = new PageImpl<VisitDTO>(Collections.emptyList());
		Page<VisitDTO> result = visitService.findAll(pageable);
		assertEquals(emptyDTOPage, result);
	}
	
	@Test
	void addNew_correctCallToRepositoryAndDTOReturnValue() throws NewVisitNotPossibleException {
		Visit newVisit = new Visit(visit.getVet(), visit.getPatient(), visit.getEpoch());
		
		// mocking vetRepo Vet result
		given(vetRepository.findById(newVisit.getVet().getId())).willReturn(Optional.of(newVisit.getVet()));
		// mocking patientRepo Patient result
		given(patientRepository.findById(newVisit.getPatient().getId())).willReturn(Optional.of(newVisit.getPatient()));
		// mocking visitRepo Visit result
		given(visitRepository.saveAndFlush(newVisit)).willReturn(visit);
		
		// verifies return value
		VisitDTO expected = modelMapper.map(visit, VisitDTO.class);
		VisitDTO result = visitService.addNew(
				newVisit.getVet().getId(), 
				newVisit.getPatient().getId(), 
				newVisit.getEpoch());
		assertEquals(expected, result);
		
		// verifies repo call
		ArgumentCaptor<Visit> visitCaptor = ArgumentCaptor.forClass(Visit.class);
		verify(visitRepository, times(1)).saveAndFlush(visitCaptor.capture());
		assertEquals(newVisit, visitCaptor.getValue());
	}
	
	@Test
	void addNew_whenNoVetOrPatient_throwsEntityNotFoundException() {
		/* Vet - not found
		 * Patient - found
		 */
		given(vetRepository.findById(1L)).willReturn(Optional.empty());
		given(patientRepository.findById(2L)).willReturn(Optional.of(patient));
		assertThrows(EntityNotFoundException.class, () -> visitService.addNew(1L, 2L, oneWeekFromNow));

		/* Vet - found
		 * Patient - not found
		 */
		given(vetRepository.findById(1L)).willReturn(Optional.of(vet));
		given(patientRepository.findById(2L)).willReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> visitService.addNew(1L, 2L, oneWeekFromNow));
	}
	
	@Test
	void addNew_whenVetOrPatientHasAllreadyVisitAtEpoch_throwsNewVisitNotPossibleException() {
		// mocking, that there is another visit at that time for chosen Vet
		given(visitRepository.findByEpochAndVetId(visit.getEpoch(), visit.getVet().getId()))
			.willReturn(Collections.singletonList(visit));

		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(
				visit.getVet().getId(), 
				visit.getPatient().getId(), 
				visit.getEpoch()));
		
	}
	
	@Test
	void addNew_whenVetIsNotActive_throwsVetNotActiveException() {
		fail("Not yet implemented");
	}
	
	@Test
	void addNew_whenVetDoesNotHavePatientsAnimalType_throwsNewVisitNotPossibleException() {
		fail("Not yet implemented");
	}
	
	@Test
	void addNew_whenEpochNotInFuture_throwsInvalidEpochException() {
		fail("Not yet implemented");
	}
	
	@Test
	void addNew_whenEpochIsOutsideVetsWorkingHours_throwsInvalidEpochException() {
		fail("Not yet implemented");
	}
	
}
