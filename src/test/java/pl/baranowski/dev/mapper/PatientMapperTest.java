package pl.baranowski.dev.mapper;

import org.junit.jupiter.api.Test;
import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Patient;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientMapperTest {

    @Test
    void toDto() {
        //given
        Patient patient = new Patient(14L, "Mariush", new AnimalType("Kox"), 13,"Ownerek", "ow@ner.ek");

        //when
        PatientDTO patientDTO = PatientMapper.INSTANCE.toDto(patient);

        //then
        assertEquals(patient.getId(), patientDTO.getId());
        assertEquals(patient.getName(), patientDTO.getName());
        assertEquals(patient.getAnimalType(), AnimalTypeMapper.INSTANCE.toEntity(patientDTO.getAnimalType()));
        assertEquals(patient.getAge(), patientDTO.getAge());
        assertEquals(patient.getOwnerName(), patientDTO.getOwnerName());
        assertEquals(patient.getOwnerEmail(), patientDTO.getOwnerEmail());
    }

    @Test
    void toEntity() {
        //given
        PatientDTO patientDTO = new PatientDTO(4L, "Burack", new AnimalTypeDTO("Shark"), 65, "Someone Important", "michael@jackson.com");
        //when
        Patient patient = PatientMapper.INSTANCE.toEntity(patientDTO);
        //then
        assertEquals(patientDTO.getId(), patient.getId());
        assertEquals(patientDTO.getName(), patient.getName());
        assertEquals(patientDTO.getAnimalType(), AnimalTypeMapper.INSTANCE.toDto(patient.getAnimalType()));
        assertEquals(patientDTO.getAge(), patient.getAge());
        assertEquals(patientDTO.getOwnerName(), patient.getOwnerName());
        assertEquals(patientDTO.getOwnerEmail(), patient.getOwnerEmail());
    }
}