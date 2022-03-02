package pl.baranowski.dev.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.entity.Patient;

@Mapper(config = MapperCentralConfig.class)
public interface PatientMapper {
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    PatientDTO toDto(Patient patient);

    @InheritInverseConfiguration
    @Mapping(target = "withId", ignore = true)
    @Mapping(target = "visits", ignore = true)
    Patient toEntity(PatientDTO patientDTO);
}
