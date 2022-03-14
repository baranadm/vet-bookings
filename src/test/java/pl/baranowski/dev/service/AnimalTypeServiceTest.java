package pl.baranowski.dev.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.animalType.AnimalTypeAlreadyExistsException;
import pl.baranowski.dev.mapper.AnimalTypeMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnimalTypeServiceTest {
    @Autowired
    AnimalTypeService animalTypeService;
    @Autowired
    AnimalTypeRepository animalTypeRepository;
    @Autowired
    AnimalTypeMapper mapper;
    private AnimalType cats;
    private AnimalType dogs;
    private List<AnimalType> animals;

    @BeforeEach
    void setUp() {
        cats = animalTypeRepository.save(new AnimalType("Cats"));
        dogs = animalTypeRepository.save(new AnimalType("Dogs"));
        animals = animalTypeRepository.findAll();
    }

    @AfterEach
    void tearDown() {
        animalTypeRepository.deleteAll();
    }

    @Test
    void findById_whenEntityExists_returnsDTO() throws NotFoundException {
        assertEquals(mapper.toDto(cats), animalTypeService.findById(cats.getId()));
    }

    @Test
    void findById_whenEntityDoesNotExists_throwsEntityNotFoundException() {
        assertThrows(NotFoundException.class, () -> animalTypeService.findById(123L));
    }

    @Test
    void findByName_whenEntityExists_returnsDTO() throws NotFoundException {
        assertEquals(mapper.toDto(cats), animalTypeService.findByName("Cats")
        );
    }

    @Test
    void findByName_whenEntitiesDoNotExist_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> animalTypeService.findByName("Ł"));
    }

    @Test
    void findByName_whenNameIsPartOfOtherEntityName_doNotReturnThatEntity() {
        assertThrows(NotFoundException.class, () -> animalTypeService.findByName("Cat"));
    }

    @Test
    void findAll_whenEntitiesExist_returnsListOfDTOs() {
        assertEquals(animals.stream().map(mapper::toDto).collect(Collectors.toList()), animalTypeService.findAll());
    }

    @Test
    void findAll_whenEntitiesDoNotExist_returnsEmptyList() {
        animalTypeRepository.deleteAll();
        assertEquals(Collections.emptyList(), animalTypeService.findAll());
    }

    @Test
    void addNew_whenNoDuplicate_returnsNewDTO() throws AnimalTypeAlreadyExistsException {
        AnimalTypeDTO result = animalTypeService.addNew("Camel");
        assertEquals("Camel", result.getName());
        assertNotNull(result.getId());
    }

    @Test
    void addNew_whenDuplicate_throwsAnimalTypeAlreadyExistsException() {
        assertThrows(AnimalTypeAlreadyExistsException.class, () -> animalTypeService.addNew(cats.getName()));
    }
}
