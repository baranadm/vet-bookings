package pl.baranowski.dev.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.baranowski.dev.builder.DoctorBuilder;
import pl.baranowski.dev.builder.DoctorDTOBuilder;
import pl.baranowski.dev.dto.DoctorsFreeSlotsDTO;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.model.DoctorsFreeSlots;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AvailableSlotsMapperTest {

    @Autowired
    AvailableSlotsMapper underTest;
    @Autowired
    DoctorMapper doctorMapper;

    @Test
    void toDto() {
        //given
        Doctor doctor = new DoctorBuilder().id(1L).name("Doctor").surname("Lubich").build();
        long mondayH10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();
        long mondayH11Y2100 = mondayH10Y2100 + 3600;
        List<Long> epochFreeTimes = Arrays.asList(mondayH10Y2100, mondayH11Y2100);
        DoctorsFreeSlots entity = new DoctorsFreeSlots(doctor, epochFreeTimes);
        //when
        DoctorsFreeSlotsDTO dto = underTest.toDto(entity);
        //then
        assertEquals(entity.getDoctor(), doctorMapper.toEntity(dto.getDoctorDTO()));
        assertEquals(entity.getEpochFreeTimes(), dto.getAvailableEpochTimes());
    }

    @Test
    void toEntity() {
        //given
        DoctorDTO doctorDTO = new DoctorDTOBuilder().id(2L).name("Not Doctor").surname("Lubich").build();
        long mondayH10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();
        long mondayH11Y2100 = mondayH10Y2100 + 3600;
        List<Long> epochFreeTimes = Arrays.asList(mondayH10Y2100, mondayH11Y2100);
        DoctorsFreeSlotsDTO dto = new DoctorsFreeSlotsDTO(doctorDTO, epochFreeTimes);
        //when
        DoctorsFreeSlots entity = underTest.toEntity(dto);
        //then
        assertEquals(dto.getDoctorDTO(), doctorMapper.toDto(entity.getDoctor()));
        assertEquals(dto.getAvailableEpochTimes(), entity.getEpochFreeTimes());
    }
}