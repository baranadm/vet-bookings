package pl.baranowski.dev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
import pl.baranowski.dev.exception.SearchRequestInvalidException;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.mapper.CustomMapper;
import pl.baranowski.dev.repository.PatientRepository;
import pl.baranowski.dev.repository.DoctorRepository;
import pl.baranowski.dev.repository.VisitRepository;


// TODO inspect, why in database every New Visit's isConfirmed = null
@SpringBootTest
class VisitServiceTest {

	@Autowired
	VisitService visitService;
	
	@Autowired
	CustomMapper mapper;

	@MockBean
	VisitRepository visitRepository;
	@MockBean
	DoctorRepository doctorRepository;
	@MockBean
	PatientRepository patientRepository;

	long mondayH10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();
	AnimalType animalType = new AnimalType(1L, "Owad");
	MedSpecialty medSpecialty = new MedSpecialty(2L, "Czółkolog");
	Doctor doctor = new Doctor(3L, "Kazik", "Montana", new BigDecimal(220), "1111111111");
	Patient patient = new Patient(4L, "Karaluch", animalType, 13, "Lubiacz Owadów", "ijegomail@sld.pl");
	Visit visit = new Visit.VisitBuilder(doctor, patient, mondayH10Y2100).build().withId(13L);
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void getById_correctCallToRepositoryAndDTOReturnValue() {
		Long id = 13L;
		given(visitRepository.findById(id)).willReturn(Optional.of(visit));

		VisitDTO expected = mapper.toDto(visit);
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
		Page<VisitDTO> expected = mockedRepoResult.map(visit -> mapper.toDto(visit));
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
	void addNew_correctCallToRepositoryAndDTOReturnValue() throws NewVisitNotPossibleException, DoctorNotActiveException {
		// new Visit without id
		Visit newVisit = new Visit.VisitBuilder(visit.getDoctor(), visit.getPatient(), visit.getEpoch()).build();

		// adds Patient's Animal Type to Doctor
		visit.getDoctor().addAnimalType(visit.getPatient().getAnimalType());
		
		// mocking doctorRepo Doctor result
		given(doctorRepository.findById(newVisit.getDoctor().getId())).willReturn(Optional.of(newVisit.getDoctor()));
		// mocking patientRepo Patient result
		given(patientRepository.findById(newVisit.getPatient().getId())).willReturn(Optional.of(newVisit.getPatient()));
		// mocking visitRepo Visit result
		given(visitRepository.saveAndFlush(newVisit)).willReturn(visit);
		
		// verifies return value
		VisitDTO expected = mapper.toDto(visit);
		VisitDTO result = visitService.addNew(
				newVisit.getDoctor().getId(), 
				newVisit.getPatient().getId(), 
				newVisit.getEpoch());
		assertEquals(expected, result);
		
		// verifies repo call
		ArgumentCaptor<Visit> visitCaptor = ArgumentCaptor.forClass(Visit.class);
		verify(visitRepository, times(1)).saveAndFlush(visitCaptor.capture());
		assertEquals(newVisit, visitCaptor.getValue());
	}
	
	@Test
	void addNew_whenNoDoctorOrPatient_throwsEntityNotFoundException() {
		/* Doctor - not found
		 * Patient - found
		 */
		given(doctorRepository.findById(1L)).willReturn(Optional.empty());
		given(patientRepository.findById(2L)).willReturn(Optional.of(patient));
		assertThrows(EntityNotFoundException.class, () -> visitService.addNew(1L, 2L, mondayH10Y2100));

		/* Doctor - found
		 * Patient - not found
		 */
		given(doctorRepository.findById(1L)).willReturn(Optional.of(doctor));
		given(patientRepository.findById(2L)).willReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> visitService.addNew(1L, 2L, mondayH10Y2100));
	}
	
	@Test
	void addNew_whenDoctorOrPatientHasAllreadyVisitAtEpoch_throwsNewVisitNotPossibleException() {
		// Doctor IS BUSY
		// mocking, that there is another visit at that time for chosen Doctor
		given(visitRepository.findByEpochAndDoctorId(visit.getEpoch(), visit.getDoctor().getId()))
			.willReturn(Collections.singletonList(visit));

		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(
				visit.getDoctor().getId(), 
				visit.getPatient().getId(), 
				visit.getEpoch()));
		
		// PATIENT IS BUSY
		// mocking, that there is another visit at that time for chosen Patient
		given(visitRepository.findByEpochAndPatientId(visit.getEpoch(), visit.getPatient().getId()))
		.willReturn(Collections.singletonList(visit));

		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(
				visit.getDoctor().getId(), 
				visit.getPatient().getId(), 
				visit.getEpoch()));
	}
	
	@Test
	void addNew_whenDoctorIsNotActive_throwsDoctorNotActiveException() {
		Doctor inactiveDoctor = new Doctor(3L, "Mały", "Zenek", new BigDecimal(100), "1111111111");
		inactiveDoctor.setActive(false);
		given(doctorRepository.findById(inactiveDoctor.getId())).willReturn(Optional.of(inactiveDoctor));
		given(patientRepository.findById(patient.getId())).willReturn(Optional.of(patient));
		
		assertThrows(DoctorNotActiveException.class, () -> visitService.addNew(inactiveDoctor.getId(), patient.getId(), mondayH10Y2100));
	}
	
	@Test
	void addNew_whenDoctorDoesNotHavePatientsAnimalType_throwsNewVisitNotPossibleException() {
		Doctor catsDoctor = new Doctor(3L, "Mały", "Zenek", new BigDecimal(100), "1111111111");
		catsDoctor.addAnimalType(new AnimalType(1L, "Kot"));
		
		given(doctorRepository.findById(catsDoctor.getId())).willReturn(Optional.of(catsDoctor));
		given(patientRepository.findById(patient.getId())).willReturn(Optional.of(patient));
		
		// patient's animalType = Owad, catsDoctor animalType = "Kot"
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(catsDoctor.getId(), patient.getId(), mondayH10Y2100));
	}
	
	@Test
	void addNew_whenEpochNotInFuture_throwsNewVisitNotPossibleException() {
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), System.currentTimeMillis()/1000 - 60)); // now - 1 minute
	}
	
	@Test
	void addNew_whenEpochIsOutsideDoctorsWorkingHoursOrDays_throwsNewVisitNotPossibleException() {
		given(doctorRepository.findById(doctor.getId())).willReturn(Optional.of(doctor));
		long epochMondayBeforeWork = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 8, 00, 00), ZoneId.systemDefault()).toEpochSecond();
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayBeforeWork));
		
		long epochMondayAfterWork = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 16, 00, 00), ZoneId.systemDefault()).toEpochSecond();
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayAfterWork));

		long epochSunday = ZonedDateTime.of(LocalDateTime.of(2100, 1, 31, 12, 00, 00), ZoneId.systemDefault()).toEpochSecond();
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), epochSunday));
	}
	
	@Test
	void addNew_whenEpochIsNotAtTheTopOfTheHour_throwsNewVisitNotPossibleException() {
		given(doctorRepository.findById(doctor.getId())).willReturn(Optional.of(doctor));
		// 1sec after the top of the hour
		long epochMondayDuringWorkPlus1s = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 15, 00, 01), ZoneId.systemDefault()).toEpochSecond();
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayDuringWorkPlus1s));

		// 2min after the top of the hour
		long epochMondayDuringWorkPlus2min = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 15, 02, 00), ZoneId.systemDefault()).toEpochSecond();
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayDuringWorkPlus2min));

	}
	
	@Test
	void findFreeSlotsForDoctor_correctCallToRepositoryAndReturnValue() throws SearchRequestInvalidException {
		// Doctor's working hours: 9-16 (so last visit is at 15:00)
		// date: 2100-01-25
		Visit first = new Visit.VisitBuilder(doctor, patient, mondayH10Y2100).build().withId(1L); // 10:00
		Visit second = new Visit.VisitBuilder(doctor, patient, mondayH10Y2100 +60*60).build().withId(2L); // 11:00
		Visit third = new Visit.VisitBuilder(doctor, patient, mondayH10Y2100 +2*60*60).build().withId(3L); // 12:00
		Visit fourth = new Visit.VisitBuilder(doctor, patient, mondayH10Y2100 +4*60*60).build().withId(4L); // 14:00
		
		doctor.addVisit(first);
		doctor.addVisit(second);
		doctor.addVisit(third);
		doctor.addVisit(fourth);
		
		List<Long> expected = new ArrayList<>();
		expected.add(mondayH10Y2100 - 60*60); // 9:00
		expected.add(mondayH10Y2100 + 3*60*60); // 13:00
		expected.add(mondayH10Y2100 + 5*60*60); // 15:00
		
		// mocking repository result
		given(doctorRepository.findById(doctor.getId())).willReturn(Optional.of(doctor));
		
		// searching start: 2100-01-25 5:00, end 2100-01-25 18:00
		List<Long> result = visitService.findFreeSlotsForDoctor(doctor, mondayH10Y2100 - 5*60*60, mondayH10Y2100 + 8*60*60, 3600L);
		assertEquals(expected, result);
		
	}

	@Test
	void findFreeSlotsForDoctor_epochsValidation_throwSearchRequestInvalidException() {
		// if start = end
		final long start = mondayH10Y2100;
		final long endEqual = start;
		assertThrows(SearchRequestInvalidException.class, () -> visitService.findFreeSlotsForDoctor(doctor, start, endEqual, 3600L));
		
		//if start > end
		final long endBefore = start - 1;
		assertThrows(SearchRequestInvalidException.class, () -> visitService.findFreeSlotsForDoctor(doctor, start, endBefore, 3600L));
		// if start < now
		final long startBeforeNow = System.currentTimeMillis()/1000 - 1;
		assertThrows(SearchRequestInvalidException.class, () -> visitService.findFreeSlotsForDoctor(doctor, startBeforeNow, endEqual, 3600L));
	}
	
}
