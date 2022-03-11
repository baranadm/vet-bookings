package pl.baranowski.dev.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pl.baranowski.dev.dto.DoctorsFreeSlotsDTO;
import pl.baranowski.dev.model.DoctorsFreeSlots;

@Mapper(config = MapperCentralConfig.class,
        uses = DoctorMapper.class)
public interface AvailableSlotsMapper {
    AvailableSlotsMapper INSTANCE = Mappers.getMapper(AvailableSlotsMapper.class);

    @Mapping(source = "epochFreeTimes", target = "availableEpochTimes")
    @Mapping(source = "doctor", target = "doctorDTO")
    DoctorsFreeSlotsDTO toDto(DoctorsFreeSlots entity);

    @InheritInverseConfiguration
    DoctorsFreeSlots toEntity(DoctorsFreeSlotsDTO dto);
}
