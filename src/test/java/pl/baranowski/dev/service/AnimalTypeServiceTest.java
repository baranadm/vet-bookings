package pl.baranowski.dev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
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

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.exception.animalType.AnimalTypeAlreadyExistsException;
import pl.baranowski.dev.mapper.AnimalTypeMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;

// TODO check, if repo is not considering "Cat" and "Cats" are equal (contains != equals!!). If so, findByName should be repaired
@SpringBootTest
class AnimalTypeServiceTest {

	@Autowired
	AnimalTypeService animalTypeService;
	
	@Autowired
	AnimalTypeMapper mapper;
	
	@MockBean
	AnimalTypeRepository animalTypeRepository;
	
	private AnimalType cats = new AnimalType(1l, "Cats");
	private AnimalType dogs = new AnimalType(2l, "Dogs");
	private List<AnimalType> animals = Arrays.asList(cats, dogs);
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void findById_whenEntityExists_returnsDTO() {
		given(animalTypeRepository.findById(1L)).willReturn(Optional.ofNullable(cats));
		assertEquals(mapper.toDto(cats), animalTypeService.findById(1L));
	}
	
	@Test
	void findById_whenEntityDoesNotExists_throwsEntityNotFoundException() {
		given(animalTypeRepository.findById(1L)).willThrow(EntityNotFoundException.class);
		assertThrows(EntityNotFoundException.class, () -> animalTypeService.findById(1L));
	}
	
	@Test
	void findByName_whenEntitiesExist_returnsListOfDTOs() {
		given(animalTypeRepository.findOneByName("Cats")).willReturn(Optional.of(cats));
		assertEquals(
				Collections.singletonList(cats).stream().map(mapper::toDto).collect(Collectors.toList()), 
				animalTypeService.findByName("Cats")
				);
	}
	
	@Test
	void findByName_whenEntitiesDoNotExist_returnsEmptyList() {
		given(animalTypeRepository.findOneByName("ł")).willReturn(Optional.empty());
		assertEquals(Collections.emptyList(), animalTypeService.findByName("ł"));
	}

	@Test
	void findAll_whenEntitiesExist_returnsListOfDTOs() {
		given(animalTypeRepository.findAll()).willReturn(animals);
		assertEquals(animals.stream().map(mapper::toDto).collect(Collectors.toList()), animalTypeService.findAll());
	}
	
	@Test
	void findAll_whenEntitiesDoNotExist_returnsEmptyList() {
		given(animalTypeRepository.findAll()).willReturn(Collections.emptyList());
		assertEquals(Collections.emptyList(), animalTypeService.findAll());
	}

	@Test
	void addNew_whenNoDuplicate_returnsNewDTO() throws AnimalTypeAlreadyExistsException {
		given(animalTypeRepository.save(dogs)).willReturn(dogs);
		AnimalTypeDTO dogsDTO = mapper.toDto(dogs);
		assertEquals(dogsDTO, animalTypeService.addNew(dogsDTO));
	}
	
	@Test
	void addNew_whenDuplicate_throwsAnimalTypeAllreadyExistsException() {
		given(animalTypeRepository.findOneByName(cats.getName())).willReturn(Optional.of(cats));
		assertThrows(AnimalTypeAlreadyExistsException.class , () -> animalTypeService.addNew(mapper.toDto(cats)));
	}
}
