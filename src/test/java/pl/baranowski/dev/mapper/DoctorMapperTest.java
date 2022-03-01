package pl.baranowski.dev.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.ap.internal.util.Collections;
import pl.baranowski.dev.builder.DoctorBuilder;
import pl.baranowski.dev.builder.DoctorDTOBuilder;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.MedSpecialty;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DoctorMapperTest {

    @Test
    void toDto_allFieldsValid() {
        // given
        Doctor doctor = new DoctorBuilder()
                .id(156L)
                .name("Johnny")
                .surname("Bravo")
                .hourlyRate(new BigDecimal(123))
                .nip("1111111111")
                .active(true)
                .build();

        AnimalType animalType = new AnimalType(1L, "Dog");
        doctor.addAnimalType(animalType);

        MedSpecialty medSpecialty = new MedSpecialty("Cardiologist");
        doctor.addMedSpecialty(medSpecialty);

        //when
        DoctorDTO doctorDTO = DoctorMapper.INSTANCE.toDto(doctor);
        //then
        assertEquals(doctor.getId(), doctorDTO.getId());
        assertEquals(doctor.getName(), doctorDTO.getName());
        assertEquals(doctor.getSurname(), doctorDTO.getSurname());
        assertEquals(doctor.getHourlyRate(), new BigDecimal(doctorDTO.getHourlyRate()));
        assertEquals(doctor.getNip(), doctorDTO.getNip());
        assertEquals(doctor.getActive(), doctorDTO.getActive());
        assertEquals(doctor.getAnimalTypes(), doctorDTO.getAnimalTypes());
        assertEquals(doctor.getMedSpecialties(), doctorDTO.getMedSpecialties());

    }

    @Test
    void toEntity_allFieldsValid() {
        // given
        AnimalType animalType = new AnimalType(1L, "Dog");
        MedSpecialty medSpecialty = new MedSpecialty("Cardiologist");
        DoctorDTO doctorDTO = new DoctorDTOBuilder()
                .id(4L)
                .name("Mark")
                .surname("Sugarberg")
                .hourlyRate("1234.00")
                .nip("1111111111")
                .active(true)
                .animalTypes(Collections.asSet(animalType))
                .medSpecialties(Collections.asSet(medSpecialty))
                .build();


        //when
        Doctor doctor = DoctorMapper.INSTANCE.toEntity(doctorDTO);
        //then
        assertEquals(doctorDTO.getId(), doctor.getId());
        assertEquals(doctorDTO.getName(), doctor.getName());
        assertEquals(doctorDTO.getSurname(), doctor.getSurname());
        assertEquals(new BigDecimal(doctorDTO.getHourlyRate()), doctor.getHourlyRate());
        assertEquals(doctorDTO.getNip(), doctor.getNip());
        assertEquals(doctorDTO.getActive(), doctor.getActive());
        assertEquals(doctorDTO.getAnimalTypes(), doctor.getAnimalTypes());
        assertEquals(doctorDTO.getMedSpecialties(), doctor.getMedSpecialties());
    }
}