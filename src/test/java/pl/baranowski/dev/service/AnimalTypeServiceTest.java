package pl.baranowski.dev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
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

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.exception.AnimalTypeAllreadyExistsException;
import pl.baranowski.dev.repository.AnimalTypeRepository;

@SpringBootTest
class AnimalTypeServiceTest {

	@Autowired
	AnimalTypeService animalTypeService;
	
	@Autowired
	ModelMapper modelMapper;
	
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
		assertEquals(modelMapper.map(cats, AnimalTypeDTO.class), modelMapper.map(animalTypeService.findById(1L), AnimalTypeDTO.class));
	}
	
	@Test
	void findById_whenEntityDoesNotExists_throwsEntityNotFoundException() {
		given(animalTypeRepository.findById(1L)).willThrow(EntityNotFoundException.class);
		assertThrows(EntityNotFoundException.class, () -> animalTypeService.findById(1L));
	}
	
	@Test
	void findByName_whenEntitiesExist_returnsListOfDTOs() {
		given(animalTypeRepository.findByName("Cats")).willReturn(Collections.singletonList(cats));
		assertEquals(
				Collections.singletonList(cats).stream().map(mapToDto).collect(Collectors.toList()), 
				animalTypeService.findByName("Cats")
				);
	}
	
	@Test
	void findByName_whenEntitiesDoNotExist_returnsEmptyList() {
		given(animalTypeRepository.findByName("ł")).willReturn(Collections.emptyList());
		assertEquals(Collections.emptyList(), animalTypeService.findByName("ł"));
	}

	@Test
	void findAll_whenEntitiesExist_returnsListOfDTOs() {
		given(animalTypeRepository.findAll()).willReturn(animals);
		assertEquals(animals.stream().map(mapToDto).collect(Collectors.toList()), animalTypeService.findAll());
	}
	
	@Test
	void findAll_whenEntitiesDoNotExist_returnsEmptyList() {
		given(animalTypeRepository.findAll()).willReturn(Collections.emptyList());
		assertEquals(Collections.emptyList(), animalTypeService.findAll());
	}

	@Test
	void addNew_whenNoDuplicate_returnsNewDTO() throws AnimalTypeAllreadyExistsException {
		given(animalTypeRepository.saveAndFlush(dogs)).willReturn(dogs);
		AnimalTypeDTO dogsDTO = mapToDto.apply(dogs);
		assertEquals(dogsDTO, animalTypeService.addNew(dogsDTO));
	}
	
	@Test
	void addNew_whenDuplicate_throwsAnimalTypeAllreadyExistsException() {
		given(animalTypeRepository.findByName(cats.getName())).willReturn(Collections.singletonList(cats));
		assertThrows(AnimalTypeAllreadyExistsException.class , () -> animalTypeService.addNew(mapToDto.apply(cats)));
	}

	private Function<AnimalType, AnimalTypeDTO> mapToDto = entity -> modelMapper.map(entity, AnimalTypeDTO.class);
//	private Function<AnimalTypeDTO, AnimalType> mapToEntity = dto -> modelMapper.map(dto, AnimalType.class);
}
