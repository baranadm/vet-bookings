package pl.baranowski.dev.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pl.baranowski.dev.dto.AvailableSlotsAtTheDoctorDTO;
import pl.baranowski.dev.model.AvailableSlotsAtTheDoctor;

@Mapper(config = MapperCentralConfig.class,
        uses = DoctorMapper.class)
public interface AvailableSlotsMapper {
    AvailableSlotsMapper INSTANCE = Mappers.getMapper(AvailableSlotsMapper.class);

    @Mapping(source = "epochFreeTimes", target = "availableEpochTimes")
    @Mapping(source = "doctor", target = "doctorDTO")
    AvailableSlotsAtTheDoctorDTO toDto(AvailableSlotsAtTheDoctor entity);

    @InheritInverseConfiguration
    AvailableSlotsAtTheDoctor toEntity(AvailableSlotsAtTheDoctorDTO dto);
}
