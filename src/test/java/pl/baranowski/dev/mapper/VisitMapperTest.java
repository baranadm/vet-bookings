package pl.baranowski.dev.mapper;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.mapstruct.ap.internal.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.baranowski.dev.builder.DoctorBuilder;
import pl.baranowski.dev.builder.DoctorDTOBuilder;
import pl.baranowski.dev.builder.VisitBuilder;
import pl.baranowski.dev.dto.*;
import pl.baranowski.dev.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VisitMapperTest {
    @Autowired
    VisitMapper underTest;
    @Autowired
    DoctorMapper doctorMapper;
    @Autowired
    PatientMapper patientMapper;

    @Test
    void toDto() {
        //given
        AnimalType animalType = new AnimalType(5L, "Bird");
        MedSpecialty medSpecialty = new MedSpecialty(6L, "urologist");
        Doctor doctor = new DoctorBuilder().name("Barack")
                .surname("Obama")
                .hourlyRate(new BigDecimal(145))
                .animalTypes(Collections.asSet(animalType))
                .medSpecialties(Collections.asSet(medSpecialty))
                .active(true)
                .build();
        Patient patient = new Patient(1L, "Zazu", animalType, 1, "Lion King", "lion@jungle.com");
        Long mondayH10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();

        Visit visit = new VisitBuilder().id(1L).doctor(doctor).patient(patient).epoch(mondayH10Y2100).build();
        //when
        VisitDTO visitDTO = underTest.toDto(visit);
        //then
        assertEquals(visit.getId(), visitDTO.getId());
        assertEquals(visit.getDoctor(), doctorMapper.toEntity(visitDTO.getDoctor()));
        assertEquals(visit.getPatient(), patientMapper.toEntity(visitDTO.getPatient()));
        assertEquals(visit.getEpoch(), visitDTO.getEpoch());
        assertEquals(visit.getDuration(), visitDTO.getDuration());
        assertEquals(visit.getIsConfirmed(), visitDTO.getConfirmed());
    }

    @Test
    void toEntity() {
        //given
        AnimalTypeDTO animalTypeDTO = new AnimalTypeDTO(5L, "Cat");
        MedSpecialtyDTO medSpecialtyDTO = new MedSpecialtyDTO(6L, "Surgeon");
        DoctorDTO doctorDTO = new DoctorDTOBuilder().name("Spider").surname("Man").nip("1111111111").animalTypes(Collections.asSet(animalTypeDTO)).medSpecialties(Collections.asSet(medSpecialtyDTO)).build();
        PatientDTO patientDTO = new PatientDTO(56L, "Smalls", animalTypeDTO, 4, "Mrs. Nobody","no@bo.dy");
        Long mondayH10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();

        VisitDTO visitDTO = new VisitDTO(3L, doctorDTO, patientDTO, mondayH10Y2100, true, 3600L);
        //when
        Visit visit = underTest.toEntity(visitDTO);
        System.out.println(visit);
        //then
        assertEquals(visitDTO.getId(), visit.getId());
        assertEquals(visitDTO.getDoctor(), doctorMapper.toDto(visit.getDoctor()));
        assertEquals(visitDTO.getPatient(), patientMapper.toDto(visit.getPatient()));
        assertEquals(visitDTO.getEpoch(), visit.getEpoch());
        assertEquals(visitDTO.getDuration(), visit.getDuration());
        assertEquals(visitDTO.getConfirmed(), visit.getIsConfirmed());
    }
}