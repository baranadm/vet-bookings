package pl.baranowski.dev.mapper;

import org.junit.jupiter.api.Test;
import pl.baranowski.dev.builder.DoctorBuilder;
import pl.baranowski.dev.builder.DoctorDTOBuilder;
import pl.baranowski.dev.dto.AvailableSlotsAtTheDoctorDTO;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.model.AvailableSlotsAtTheDoctor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailableSlotsMapperTest {

    @Test
    void toDto() {
        //given
        Doctor doctor = new DoctorBuilder().id(1L).name("Doctor").surname("Lubich").build();
        long mondayH10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();
        long mondayH11Y2100 = mondayH10Y2100 + 3600;
        List<Long> epochFreeTimes = Arrays.asList(mondayH10Y2100, mondayH11Y2100);
        AvailableSlotsAtTheDoctor entity = new AvailableSlotsAtTheDoctor(doctor,epochFreeTimes);
        //when
        AvailableSlotsAtTheDoctorDTO dto = AvailableSlotsMapper.INSTANCE.toDto(entity);
        //then
        assertEquals(entity.getDoctor(), DoctorMapper.INSTANCE.toEntity(dto.getDoctorDTO()));
        assertEquals(entity.getEpochFreeTimes(), dto.getAvailableEpochTimes());
    }

    @Test
    void toEntity() {
        //given
        DoctorDTO doctorDTO = new DoctorDTOBuilder().id(2L).name("Not Doctor").surname("Lubich").build();
        long mondayH10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();
        long mondayH11Y2100 = mondayH10Y2100 + 3600;
        List<Long> epochFreeTimes = Arrays.asList(mondayH10Y2100, mondayH11Y2100);
        AvailableSlotsAtTheDoctorDTO dto = new AvailableSlotsAtTheDoctorDTO(doctorDTO, epochFreeTimes);
        //when
        AvailableSlotsAtTheDoctor entity = AvailableSlotsMapper.INSTANCE.toEntity(dto);
        //then
        assertEquals(dto.getDoctorDTO(), DoctorMapper.INSTANCE.toDto(entity.getDoctor()));
        assertEquals(dto.getAvailableEpochTimes(), entity.getEpochFreeTimes());
    }
}