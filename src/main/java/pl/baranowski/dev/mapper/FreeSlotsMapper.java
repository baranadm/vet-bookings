package pl.baranowski.dev.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.baranowski.dev.dto.DoctorsFreeSlotsDTO;
import pl.baranowski.dev.model.DoctorsFreeSlots;

@Mapper(config = MapperCentralConfig.class,
        uses = {DoctorMapper.class})
public interface FreeSlotsMapper {
    FreeSlotsMapper INSTANCE = Mappers.getMapper(FreeSlotsMapper.class);

    @Mapping(source = "epochFreeTimes", target = "availableEpochTimes")
    @Mapping(source = "doctor", target = "doctorDTO")
    DoctorsFreeSlotsDTO toDto(DoctorsFreeSlots doctorsFreeSlots);

    @InheritInverseConfiguration
    DoctorsFreeSlots toEntity(DoctorsFreeSlotsDTO doctorsFreeSlotsDTO);
}
