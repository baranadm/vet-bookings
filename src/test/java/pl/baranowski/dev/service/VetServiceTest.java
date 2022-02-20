package pl.baranowski.dev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.exception.DoubledSpecialtyException;
import pl.baranowski.dev.exception.NIPExistsException;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.mapper.CustomMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.MedSpecialtyRepository;
import pl.baranowski.dev.repository.DoctorRepository;

@SpringBootTest
class DoctorServiceTest {

	@MockBean
	DoctorRepository doctorRepository;
	
	@MockBean
	AnimalTypeRepository animalTypeRepository;
	
	@MockBean
	MedSpecialtyRepository medSpecialtyRepository;
	
	@Autowired
	DoctorService doctorService;
	
	@Autowired
	CustomMapper mapper;
	
	private final Doctor mostowiak = new Doctor(1L, "Marek", "Mostówiak", new BigDecimal(150.0), "1181328620");
	private List<DoctorDTO> doctorsList;
	
	@BeforeEach
	void setUp() throws Exception {
		doctorsList = new ArrayList<>();
		
		doctorsList.add(new DoctorDTO.Builder("Robert", "Kubica").hourlyRate("100000.00").nip("1213141516").build());
		doctorsList.add(new DoctorDTO.Builder("Mirosław", "Rosomak").hourlyRate("100.00").nip("0987654321").build());
		doctorsList.add(new DoctorDTO.Builder("Mamadou", "Urghabananandi").hourlyRate("40.00").nip("5566557755").build());
		doctorsList.add(new DoctorDTO.Builder("C", "J").hourlyRate("123.45").nip("1122334455").build());
		
	}

	@Test
	void test_mappings() {
		DoctorDTO dto = mapper.toDto(mostowiak);
		assertEquals(mostowiak, mapper.toEntity(dto));
	}

	@Test
	void getById_whenValidId_returnsDTOfromOptional() {
		Long id = 1L;
		Optional<Doctor> expected = Optional.of(mostowiak);
		given(doctorRepository.findById(id)).willReturn(expected);
		DoctorDTO result = doctorService.getById(id);
		assertEquals(mapper.toDto(expected.get()), result);
	}

	@Test
	void getById_whenNoEntityWithGivenId_throwsEntityNotFoundException() {
		Long id = 1L;
		
		given(doctorRepository.findById(id)).willReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> doctorService.getById(id));
	}
	
	@Test
	void findAll_ifEntitiesFound_returnsPageWithListOfDTOs() {
		Pageable pageable = PageRequest.of(0, 2);
		List<Doctor> entitiesDoctorsList = doctorsList.stream().map(mapper::toEntity).collect(Collectors.toList());
		Page<Doctor> repoResult = new PageImpl<>(entitiesDoctorsList, pageable, entitiesDoctorsList.size());

		given(doctorRepository.findAll(pageable)).willReturn(repoResult);

		Page<DoctorDTO> expected = new PageImpl<>(doctorsList, pageable, doctorsList.size());
		Page<DoctorDTO> result = doctorService.findAll(pageable);
		
		assertPagesEquals(expected, result);
	}
	
	@Test
	void findAll_ifNoEntitiesFound_returnsEmptyPage() {
		Pageable pageable = PageRequest.of(0, 2);
		Page<Doctor> repoResult = new PageImpl<Doctor>(Collections.emptyList(), pageable, 0);
		Page<DoctorDTO> expected = repoResult.map(mapper::toDto);
		
		given(doctorRepository.findAll(pageable)).willReturn(repoResult);
		assertPagesEquals(expected, doctorService.findAll(pageable));
	}
	
	@Test
	void addNew_ifOK_returnDTO() throws NIPExistsException {
		given(doctorRepository.saveAndFlush(mostowiak)).willReturn(mostowiak);
		DoctorDTO expected = mapper.toDto(mostowiak);
		DoctorDTO result = doctorService.addNew(mapper.toDto(mostowiak));
		assertEquals(expected, result);
	}
	
	@Test
	void addNew_ifNipExists_throwNIPExistsException() {
		// simulation of existing NIP in database
		given(doctorRepository.findByNip(mostowiak.getNip())).willReturn(Collections.singletonList(mostowiak));
		assertThrows(NIPExistsException.class, () -> doctorService.addNew(mapper.toDto(mostowiak)));
	}
	
	@Test
	void fire_ifEntryExistsAndIsActive_setsActiveToFalse() throws DoctorNotActiveException {
		Doctor active = mostowiak;
		active.setActive(true);
		
		given(doctorRepository.findById(active.getId())).willReturn(Optional.of(active));
		
		doctorService.fire(active.getId());
		assertFalse(doctorRepository.findById(active.getId()).get().getActive());
	}
	
	@Test
	void fire_ifEntryExistsAndIsInactive_throwsDoctorNotActiveException() {
		Doctor fired = mostowiak;
		fired.setActive(false);

		given(doctorRepository.findById(fired.getId())).willReturn(Optional.of(fired));
		
		assertThrows(DoctorNotActiveException.class, () -> doctorService.fire(fired.getId()));
	}
	
	@Test
	void fire_ifNoEntry_throwsEntityNotFoundException() {
		given(doctorRepository.findById(1L)).willReturn(Optional.empty());
//		doctorService.fire(1L);
		assertThrows(EntityNotFoundException.class, () -> doctorService.fire(1L));
	}
	
	@Test
	void addAnimalType_whenDoctorAndAnimalTypeExists_returnsTrueOnSuccess() throws DoctorNotActiveException, DoubledSpecialtyException {
		Doctor catsDoctor = new Doctor(mostowiak.getId(), mostowiak.getName(), mostowiak.getSurname(), mostowiak.getHourlyRate(), mostowiak.getNip());
		assertEquals(mostowiak, catsDoctor);

		AnimalType pet = new AnimalType(1L, "Cats");
		catsDoctor.addAnimalType(pet);
		
		given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
		given(animalTypeRepository.findById(pet.getId())).willReturn(Optional.ofNullable(pet));
		given(doctorRepository.saveAndFlush(catsDoctor)).willReturn(catsDoctor);
		
		DoctorDTO result = doctorService.addAnimalType(mostowiak.getId(), pet.getId());
		
		assertEquals(mapper.toDto(catsDoctor), result);
	}
	
	@Test
	void addAnimalType_whenAnimalTypeNotFound_throwsEntityNotFoundException() {
		// mocks no animalType in database
		given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
		given(animalTypeRepository.findById(1L)).willReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> doctorService.addAnimalType(mostowiak.getId(), 1L));
	}
	
	@Test
	void addAnimalType_whenDoctorNotFound_throwsEntityNotFoundException() {
		given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.empty());
		AnimalType animalType = new AnimalType(1L, "dogs");
		given(animalTypeRepository.findById(animalType.getId())).willReturn(Optional.ofNullable(animalType));
		
		assertThrows(EntityNotFoundException.class, () -> doctorService.addAnimalType(mostowiak.getId(), animalType.getId()));
	}
	
	@Test
	void addAnimalType_whenDoctorHasAnimalType_throwsDoubledSpecialtyException() {
		AnimalType pet = new AnimalType(1L, "Dogs");
		Doctor dogsDoctor = mostowiak;
		dogsDoctor.addAnimalType(pet);
		
		given(animalTypeRepository.findById(1L)).willReturn(Optional.ofNullable(pet));
		given(doctorRepository.findById(dogsDoctor.getId())).willReturn(Optional.ofNullable(dogsDoctor));
		
		assertThrows(DoubledSpecialtyException.class, () -> doctorService.addAnimalType(dogsDoctor.getId(), pet.getId()));
	}
	
	@Test
	void addAnimalType_whenDoctorIsNotActive_throwsDoctorIsNotActiveException() {
		Doctor inactive = mostowiak;
		inactive.setActive(false);
		AnimalType pet = new AnimalType(1L, "Dogs");
		
		given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
		given(animalTypeRepository.findById(pet.getId())).willReturn(Optional.ofNullable(pet));
		
		assertThrows(DoctorNotActiveException.class, () -> doctorService.addAnimalType(mostowiak.getId(), pet.getId()));
		
	}
	
	
	
	@Test
	void addMedSpecialty_whenDoctorAndAnimalTypeExists_returnsTrueOnSuccess() throws DoctorNotActiveException, DoubledSpecialtyException {
		Doctor cardioDoctor = new Doctor(mostowiak.getId(), mostowiak.getName(), mostowiak.getSurname(), mostowiak.getHourlyRate(), mostowiak.getNip());
		assertEquals(mostowiak, cardioDoctor);

		MedSpecialty ms = new MedSpecialty(1L, "Cardio");
		cardioDoctor.addMedSpecialty(ms);
		
		given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
		given(medSpecialtyRepository.findById(ms.getId())).willReturn(Optional.ofNullable(ms));
		given(doctorRepository.saveAndFlush(cardioDoctor)).willReturn(cardioDoctor);
		
		DoctorDTO result = doctorService.addMedSpecialty(mostowiak.getId(), ms.getId());
		
		assertEquals(mapper.toDto(cardioDoctor), result);
	}
	
	@Test
	void addMedSpecialty_whenMedSpecialtyNotFound_throwsEntityNotFoundException() {
		// mocks no medSpecialty in database
		given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
		given(medSpecialtyRepository.findById(1L)).willReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> doctorService.addMedSpecialty(mostowiak.getId(), 1L));
	}
	
	@Test
	void addMedSpecialty_whenDoctorNotFound_throwsEntityNotFoundException() {
		given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.empty());
		MedSpecialty medSpecialty = new MedSpecialty(1L, "Cardio");
		given(medSpecialtyRepository.findById(medSpecialty.getId())).willReturn(Optional.ofNullable(medSpecialty));
		
		assertThrows(EntityNotFoundException.class, () -> doctorService.addMedSpecialty(mostowiak.getId(), medSpecialty.getId()));
	}
	
	@Test
	void addMedSpecialty_whenDoctorHasMedSpecialty_throwsDoubledSpecialtyException() {
		MedSpecialty ms = new MedSpecialty(1L, "Cardio");
		Doctor cardioDoctor = mostowiak;
		cardioDoctor.addMedSpecialty(ms);
		
		given(medSpecialtyRepository.findById(1L)).willReturn(Optional.ofNullable(ms));
		given(doctorRepository.findById(cardioDoctor.getId())).willReturn(Optional.ofNullable(cardioDoctor));
		
		assertThrows(DoubledSpecialtyException.class, () -> doctorService.addMedSpecialty(cardioDoctor.getId(), ms.getId()));
	}
	
	@Test
	void addMedSpecialty_whenDoctorIsNotActive_throwsDoctorIsNotActiveException() {
		Doctor inactive = mostowiak;
		inactive.setActive(false);
		MedSpecialty ms = new MedSpecialty(1L, "Cardio");
		
		given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
		given(medSpecialtyRepository.findById(ms.getId())).willReturn(Optional.ofNullable(ms));
		
		assertThrows(DoctorNotActiveException.class, () -> doctorService.addMedSpecialty(mostowiak.getId(), ms.getId()));
		
	}

	@Test
	void testMappings() {
		assertEquals(mostowiak, mapper.toEntity(mapper.toDto(mostowiak)));
	}
	private void assertPagesEquals(Page<DoctorDTO> expected, Page<DoctorDTO> result) {
		assertEquals(expected.get().collect(Collectors.toList()), result.get().collect(Collectors.toList()));
		assertEquals(expected.getPageable(), result.getPageable());
	}

}
