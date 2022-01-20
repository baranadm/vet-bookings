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
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.exception.DoubledSpecialtyException;
import pl.baranowski.dev.exception.NIPExistsException;
import pl.baranowski.dev.exception.VetNotActiveException;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.MedSpecialtyRepository;
import pl.baranowski.dev.repository.VetRepository;

@SpringBootTest
class VetServiceTest {

	@MockBean
	VetRepository vetRepository;
	
	@MockBean
	AnimalTypeRepository animalTypeRepository;
	
	@MockBean
	MedSpecialtyRepository medSpecialtyRepository;
	
	@Autowired
	VetService vetService;
	
	@Autowired
	ModelMapper modelMapper;
	
	private final Vet mostowiak = new Vet(1L, "Marek", "Mostówiak", new BigDecimal(150), "1181328620");
	private List<VetDTO> vetsList;
	
	@BeforeEach
	void setUp() throws Exception {
		vetsList = new ArrayList<>();
		vetsList.add(new VetDTO("Robert", "Kubica", new BigDecimal(100000), "1213141516"));
		vetsList.add(new VetDTO("Mirosław", "Rosomak", new BigDecimal(100.0), "0987654321"));
		vetsList.add(new VetDTO("Mamadou", "Urghabananandi", new BigDecimal(40.), "5566557755"));
		vetsList.add(new VetDTO("C", "J", new BigDecimal(123.45), "1122334455"));
	}

	@Test
	void test_mappings() {
		VetDTO dto = vetService.mapToDTO.apply(mostowiak);
		assertEquals(mostowiak, vetService.mapToEntity.apply(dto));
	}

	@Test
	void getById_whenValidId_returnsDTOfromOptional() {
		Long id = 1L;
		Optional<Vet> expected = Optional.of(mostowiak);
		given(vetRepository.findById(id)).willReturn(expected);

		VetDTO result = vetService.getById(id);
		assertEquals(mapToDTO.apply(expected.get()), result);
	}

	@Test
	void getById_whenNoEntityWithGivenId_throwsEntityNotFoundException() {
		Long id = 1L;
		
		given(vetRepository.findById(id)).willReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> vetService.getById(id));
	}
	
	@Test
	void findAll_ifEntitiesFound_returnsPageWithListOfDTOs() {
		Pageable pageable = PageRequest.of(0, 2);
		List<Vet> entitiesVetsList = vetsList.stream().map(mapToEntity).collect(Collectors.toList());
		Page<Vet> repoResult = new PageImpl<>(entitiesVetsList, pageable, entitiesVetsList.size());

		given(vetRepository.findAll(pageable)).willReturn(repoResult);

		Page<VetDTO> expected = new PageImpl<>(vetsList, pageable, vetsList.size());
		Page<VetDTO> result = vetService.findAll(pageable);
		
		assertPagesEquals(expected, result);
	}
	
	@Test
	void findAll_ifNoEntitiesFound_returnsEmptyPage() {
		Pageable pageable = PageRequest.of(0, 2);
		Page<Vet> repoResult = new PageImpl<Vet>(Collections.emptyList(), pageable, 0);
		Page<VetDTO> expected = repoResult.map(mapToDTO);
		
		given(vetRepository.findAll(pageable)).willReturn(repoResult);
		assertPagesEquals(expected, vetService.findAll(pageable));
	}
	
	@Test
	void addNew_ifOK_returnDTO() throws NIPExistsException {
		given(vetRepository.saveAndFlush(mostowiak)).willReturn(mostowiak);
		VetDTO expected = mapToDTO.apply(mostowiak);
		VetDTO result = vetService.addNew(mapToDTO.apply(mostowiak));
		assertEquals(expected, result);
	}
	
	@Test
	void addNew_ifNipExists_throwNIPExistsException() {
		// simulation of existing NIP in database
		given(vetRepository.findByNip(mostowiak.getNip())).willReturn(Collections.singletonList(mostowiak));
		assertThrows(NIPExistsException.class, () -> vetService.addNew(mapToDTO.apply(mostowiak)));
	}
	
	@Test
	void fire_ifEntryExistsAndIsActive_setsActiveToFalse() throws VetNotActiveException {
		Vet active = mostowiak;
		active.setActive(true);
		
		given(vetRepository.findById(active.getId())).willReturn(Optional.of(active));
		
		vetService.fire(active.getId());
		assertFalse(vetRepository.findById(active.getId()).get().getActive());
	}
	
	@Test
	void fire_ifEntryExistsAndIsInactive_throwsVetNotActiveException() {
		Vet fired = mostowiak;
		fired.setActive(false);

		given(vetRepository.findById(fired.getId())).willReturn(Optional.of(fired));
		
		assertThrows(VetNotActiveException.class, () -> vetService.fire(fired.getId()));
	}
	
	@Test
	void fire_ifNoEntry_throwsEntityNotFoundException() {
		given(vetRepository.findById(1L)).willReturn(Optional.empty());
//		vetService.fire(1L);
		assertThrows(EntityNotFoundException.class, () -> vetService.fire(1L));
	}
	
	@Test
	void addAnimalType_whenVetAndAnimalTypeExists_returnsTrueOnSuccess() throws VetNotActiveException, DoubledSpecialtyException {
		Vet catsVet = new Vet(mostowiak.getId(), mostowiak.getName(), mostowiak.getSurname(), mostowiak.getHourlyRate(), mostowiak.getNip());
		assertEquals(mostowiak, catsVet);

		AnimalType pet = new AnimalType(1L, "Cats");
		catsVet.addAnimalType(pet);
		
		given(vetRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
		given(animalTypeRepository.findById(pet.getId())).willReturn(Optional.ofNullable(pet));
		given(vetRepository.saveAndFlush(catsVet)).willReturn(catsVet);
		
		VetDTO result = vetService.addAnimalType(mostowiak.getId(), pet.getId());
		
		assertEquals(mapToDTO.apply(catsVet), result);
	}
	
	@Test
	void addAnimalType_whenAnimalTypeNotFound_throwsEntityNotFoundException() {
		// mocks no animalType in database
		given(vetRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
		given(animalTypeRepository.findById(1L)).willReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> vetService.addAnimalType(mostowiak.getId(), 1L));
	}
	
	@Test
	void addAnimalType_whenVetNotFound_throwsEntityNotFoundException() {
		given(vetRepository.findById(mostowiak.getId())).willReturn(Optional.empty());
		AnimalType animalType = new AnimalType(1L, "dogs");
		given(animalTypeRepository.findById(animalType.getId())).willReturn(Optional.ofNullable(animalType));
		
		assertThrows(EntityNotFoundException.class, () -> vetService.addAnimalType(mostowiak.getId(), animalType.getId()));
	}
	
	@Test
	void addAnimalType_whenVetHasAnimalType_throwsDoubledSpecialtyException() {
		AnimalType pet = new AnimalType(1L, "Dogs");
		Vet dogsVet = mostowiak;
		dogsVet.addAnimalType(pet);
		
		given(animalTypeRepository.findById(1L)).willReturn(Optional.ofNullable(pet));
		given(vetRepository.findById(dogsVet.getId())).willReturn(Optional.ofNullable(dogsVet));
		
		assertThrows(DoubledSpecialtyException.class, () -> vetService.addAnimalType(dogsVet.getId(), pet.getId()));
	}
	
	@Test
	void addAnimalType_whenVetIsNotActive_throwsVetIsNotActiveException() {
		Vet inactive = mostowiak;
		inactive.setActive(false);
		AnimalType pet = new AnimalType(1L, "Dogs");
		
		given(vetRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
		given(animalTypeRepository.findById(pet.getId())).willReturn(Optional.ofNullable(pet));
		
		assertThrows(VetNotActiveException.class, () -> vetService.addAnimalType(mostowiak.getId(), pet.getId()));
		
	}
	
	private void assertPagesEquals(Page<VetDTO> expected, Page<VetDTO> result) {
		assertEquals(expected.get().collect(Collectors.toList()), result.get().collect(Collectors.toList()));
		assertEquals(expected.getPageable(), result.getPageable());
	}
	
	private Function<VetDTO, Vet> mapToEntity = dto -> modelMapper.map(dto, Vet.class);
	private Function<Vet, VetDTO> mapToDTO = entity -> modelMapper.map(entity, VetDTO.class);


}
