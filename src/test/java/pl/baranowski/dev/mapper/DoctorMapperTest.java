package pl.baranowski.dev.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.ap.internal.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.baranowski.dev.builder.DoctorBuilder;
import pl.baranowski.dev.builder.DoctorDTOBuilder;
import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.MedSpecialty;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DoctorMapperTest {
    @Autowired
    AnimalTypeMapper animalTypeMapper;
    @Autowired
    MedSpecialtyMapper medSpecialtyMapper;
    @Autowired
    DoctorMapper underTest;

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
        DoctorDTO doctorDTO = underTest.toDto(doctor);
        //then
        assertEquals(doctor.getId(), doctorDTO.getId());
        assertEquals(doctor.getName(), doctorDTO.getName());
        assertEquals(doctor.getSurname(), doctorDTO.getSurname());
        assertEquals(doctor.getHourlyRate(), new BigDecimal(doctorDTO.getHourlyRate()));
        assertEquals(doctor.getNip(), doctorDTO.getNip());
        assertEquals(doctor.getActive(), doctorDTO.getActive());

        Set<AnimalType> animalTypes = doctorDTO.getAnimalTypes().stream().map(animalTypeMapper::toEntity).collect(Collectors.toSet());
        assertEquals(doctor.getAnimalTypes(), animalTypes);

        Set<MedSpecialty> medSpecialties = doctorDTO.getMedSpecialties().stream().map(medSpecialtyMapper::toEntity).collect(Collectors.toSet());
        assertEquals(doctor.getMedSpecialties(), medSpecialties);

    }

    @Test
    void toEntity_allFieldsValid() {
        // given
        AnimalTypeDTO animalTypeDTO = new AnimalTypeDTO(1L, "Dog");
        MedSpecialtyDTO medSpecialtyDTO = new MedSpecialtyDTO("Cardiologist");
        DoctorDTO doctorDTO = new DoctorDTOBuilder()
                .id(4L)
                .name("Mark")
                .surname("Sugarberg")
                .hourlyRate("1234.00")
                .nip("1111111111")
                .active(true)
                .animalTypes(Collections.asSet(animalTypeDTO))
                .medSpecialties(Collections.asSet(medSpecialtyDTO))
                .build();


        //when
        Doctor doctor = underTest.toEntity(doctorDTO);
        //then
        assertEquals(doctorDTO.getId(), doctor.getId());
        assertEquals(doctorDTO.getName(), doctor.getName());
        assertEquals(doctorDTO.getSurname(), doctor.getSurname());
        assertEquals(new BigDecimal(doctorDTO.getHourlyRate()), doctor.getHourlyRate());
        assertEquals(doctorDTO.getNip(), doctor.getNip());
        assertEquals(doctorDTO.getActive(), doctor.getActive());

        Set<AnimalTypeDTO> animalTypeDTOs = doctor.getAnimalTypes().stream().map(animalTypeMapper::toDto).collect(
                Collectors.toSet());
        assertEquals(doctorDTO.getAnimalTypes(), animalTypeDTOs);

        Set<MedSpecialtyDTO> medSpecialtyDTOs = doctor.getMedSpecialties().stream().map(medSpecialtyMapper::toDto).collect(
                Collectors.toSet());
        assertEquals(doctorDTO.getMedSpecialties(), medSpecialtyDTOs);
    }
}