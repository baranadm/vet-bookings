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
import java.util.Collections;
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

import pl.baranowski.dev.builder.DoctorBuilder;
import pl.baranowski.dev.builder.VisitBuilder;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
import pl.baranowski.dev.repository.DoctorRepository;
import pl.baranowski.dev.repository.PatientRepository;
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
	DoctorService doctorService;
	@MockBean
	PatientRepository patientRepository;
	@MockBean
	PatientService patientService;

	private long mondayH10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();
	private AnimalType animalType;
	private MedSpecialty medSpecialty;
	private Doctor doctor;
	private Patient patient;
	private Visit visit;
	
	@BeforeEach
	void setUp() throws Exception {
		animalType = new AnimalType(1L, "Owad");
		medSpecialty = new MedSpecialty(2L, "Czółkolog");
		doctor = new DoctorBuilder().name("Kazik").surname("Montana").nip("1111111111").hourlyRate(new BigDecimal(220)).id(3L).build();
		doctor.addAnimalType(animalType);
		doctor.addMedSpecialty(medSpecialty);
		patient = new Patient(4L, "Karaluch", animalType, 13, "Lubiacz Owadów", "ijegomail@sld.pl");
		visit = new VisitBuilder().doctor(doctor).patient(patient).epoch(mondayH10Y2100).build().withId(13L);
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
	
	
	// TODO correct this one
	@Test
	void addNew_correctCallToRepositoryAndDTOReturnValue() throws NewVisitNotPossibleException, DoctorNotActiveException {
		// new Visit without id
		Visit newVisit = new VisitBuilder().doctor(visit.getDoctor()).patient(visit.getPatient()).epoch(visit.getEpoch()).build();

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
		given(doctorService.get(1L)).willThrow(new EntityNotFoundException());
		given(patientService.get(patient.getId())).willReturn(patient);
		assertThrows(EntityNotFoundException.class, () -> visitService.addNew(1L, patient.getId(), mondayH10Y2100));

		/* Doctor - found
		 * Patient - not found
		 */
		given(doctorService.get(doctor.getId())).willReturn(doctor);
		given(patientService.get(2L)).willThrow(new EntityNotFoundException());
		assertThrows(EntityNotFoundException.class, () -> visitService.addNew(doctor.getId(), 2L, mondayH10Y2100));
	}
	
	@Test
	void addNew_whenDoctorOrPatientHasAllreadyVisitAtEpoch_throwsNewVisitNotPossibleException() throws NewVisitNotPossibleException, DoctorNotActiveException {
		// given
		AnimalType dog = new AnimalType("Dog");
		Doctor doctorJohn = new DoctorBuilder().name("John").surname("Scott").nip("1111111111").hourlyRate(new BigDecimal(456)).id(1L).build();
		doctorJohn.addAnimalType(dog);
		Patient patientRon = new Patient(2L, "Ron", dog, 123, "Harry P.", "i@like.sl");
		
		given(doctorService.get(doctorJohn.getId())).willReturn(doctorJohn);
		given(patientService.get(patientRon.getId())).willReturn(patientRon);
		
		//when doctor is busy
		Visit visitAt10 = new VisitBuilder().doctor(doctorJohn).patient(patientRon).epoch(mondayH10Y2100).build();
		doctorJohn.addVisit(visitAt10);
		
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(
				doctorJohn.getId(), 
				patientRon.getId(), 
				mondayH10Y2100));
		
		// when patient is busy
		Long mondayH11Y2100 = mondayH10Y2100 + 3600;
		
		Visit visitAt11 = new VisitBuilder().doctor(doctorJohn).patient(patientRon).epoch(mondayH11Y2100).build();
		patientRon.addVisit(visitAt11);
		
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(
				doctorJohn.getId(), 
				patientRon.getId(), 
				mondayH11Y2100));
	}
	
	@Test
	void addNew_whenDoctorIsNotActive_throwsDoctorNotActiveException() {
		Doctor inactiveDoctor = new DoctorBuilder().name("Mały").surname("Zenek").nip("1111111111").id(3L).hourlyRate(new BigDecimal(100)).build();
		inactiveDoctor.setActive(false);
		given(doctorService.get(inactiveDoctor.getId())).willReturn(inactiveDoctor);
		given(patientService.get(patient.getId())).willReturn(patient);
		
		assertThrows(DoctorNotActiveException.class, () -> visitService.addNew(inactiveDoctor.getId(), patient.getId(), mondayH10Y2100));
	}
	
	@Test
	void addNew_whenDoctorDoesNotHavePatientsAnimalType_throwsNewVisitNotPossibleException() {
		Doctor catsDoctor = new DoctorBuilder().name("Mały").surname("Zenek").nip("1111111111").id(3L).hourlyRate(new BigDecimal(100)).build();
		catsDoctor.addAnimalType(new AnimalType(1L, "Kot"));
		
		given(doctorService.get(catsDoctor.getId())).willReturn(catsDoctor);
		given(patientService.get(patient.getId())).willReturn(patient);
		
		// patient's animalType = Owad, catsDoctor animalType = "Kot"
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(catsDoctor.getId(), patient.getId(), mondayH10Y2100));
	}
	
	@Test
	void addNew_whenEpochNotInFuture_throwsNewVisitNotPossibleException() {
		given(doctorService.get(doctor.getId())).willReturn(doctor);
		given(patientService.get(patient.getId())).willReturn(patient);
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), System.currentTimeMillis()/1000 - 60)); // now - 1 minute
	}
	
	@Test
	void addNew_whenEpochIsOutsideDoctorsWorkingHoursOrDays_throwsNewVisitNotPossibleException() {
		given(doctorService.get(doctor.getId())).willReturn(doctor);
		given(patientService.get(patient.getId())).willReturn(patient);
		
		long epochMondayBeforeWork = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 8, 00, 00), ZoneId.systemDefault()).toEpochSecond();
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayBeforeWork));
		
		long epochMondayAfterWork = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 16, 00, 00), ZoneId.systemDefault()).toEpochSecond();
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayAfterWork));

		long epochSunday = ZonedDateTime.of(LocalDateTime.of(2100, 1, 31, 12, 00, 00), ZoneId.systemDefault()).toEpochSecond();
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), epochSunday));
	}
	
	@Test
	void addNew_whenEpochIsNotAtTheTopOfTheHour_throwsNewVisitNotPossibleException() {
		given(doctorService.get(doctor.getId())).willReturn(doctor);
		given(patientService.get(doctor.getId())).willReturn(patient);
		// 1sec after the top of the hour
		long epochMondayDuringWorkPlus1s = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 15, 00, 01), ZoneId.systemDefault()).toEpochSecond();
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayDuringWorkPlus1s));

		// 2min after the top of the hour
		long epochMondayDuringWorkPlus2min = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 15, 02, 00), ZoneId.systemDefault()).toEpochSecond();
		assertThrows(NewVisitNotPossibleException.class, () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayDuringWorkPlus2min));

	}
	
}
