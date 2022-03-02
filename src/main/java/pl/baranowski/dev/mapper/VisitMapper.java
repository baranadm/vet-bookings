package pl.baranowski.dev.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.Visit;

@Mapper(config = MapperCentralConfig.class,
        uses = {DoctorMapper.class, PatientMapper.class})
public interface VisitMapper {
    VisitMapper INSTANCE = Mappers.getMapper(VisitMapper.class);

    @Mapping(source = "isConfirmed", target = "confirmed")
    @Mapping(target = "withId", ignore = true)
    VisitDTO toDto(Visit visit);

    @InheritInverseConfiguration
    Visit toEntity(VisitDTO visitDTO);
}
