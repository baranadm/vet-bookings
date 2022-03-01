package pl.baranowski.dev.mapper;

import org.junit.jupiter.api.Test;
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
        assertEquals(patient.getAnimalType(), patientDTO.getAnimalType());
        assertEquals(patient.getAge(), patientDTO.getAge());
        assertEquals(patient.getOwnerName(), patientDTO.getOwnerName());
        assertEquals(patient.getOwnerEmail(), patientDTO.getOwnerEmail());
    }

    @Test
    void toEntity() {
    }
}