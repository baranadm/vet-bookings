package pl.baranowski.dev.mapper;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.entity.AnimalType;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTypeMapperTest {

    @Test
    void toDto() {
        //given
        AnimalType animalType = new AnimalType(13L,"Squirrel");

        //when
        AnimalTypeDTO animalTypeDTO = AnimalTypeMapper.INSTANCE.toDto(animalType);
        //then
        assertEquals(animalType.getId(), animalTypeDTO.getId());
        assertEquals(animalType.getName(), animalTypeDTO.getName());
    }

    @Test
    void toEntity() {
        //given
        AnimalTypeDTO animalTypeDTO = new AnimalTypeDTO(1567L, "Sheep");

        //when
        AnimalType animalType = AnimalTypeMapper.INSTANCE.toEntity(animalTypeDTO);
        //then
        assertEquals(animalTypeDTO.getId(), animalType.getId());
        assertEquals(animalTypeDTO.getName(), animalType.getName());
    }
}