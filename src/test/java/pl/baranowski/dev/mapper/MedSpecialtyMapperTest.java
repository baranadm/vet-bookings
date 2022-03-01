package pl.baranowski.dev.mapper;

import org.junit.jupiter.api.Test;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.MedSpecialty;

import static org.junit.jupiter.api.Assertions.*;

class MedSpecialtyMapperTest {

    @Test
    void toDto() {
        //given
        MedSpecialty medSpecialty = new MedSpecialty(45L,"Cardiologist");
        //when
        MedSpecialtyDTO medSpecialtyDTO = MedSpecialtyMapper.INSTANCE.toDto(medSpecialty);
        //then
        assertEquals(medSpecialty.getId(), medSpecialtyDTO.getId());
        assertEquals(medSpecialty.getName(), medSpecialtyDTO.getName());
    }

    @Test
    void toEntity() {
        //given
        MedSpecialtyDTO medSpecialtyDTO = new MedSpecialtyDTO(56L, "Neurologist");
        //when
        MedSpecialty medSpecialty = MedSpecialtyMapper.INSTANCE.toEntity(medSpecialtyDTO);
        //then
        assertEquals(medSpecialtyDTO.getId(), medSpecialty.getId());
        assertEquals(medSpecialtyDTO.getName(), medSpecialty.getName());
    }
}