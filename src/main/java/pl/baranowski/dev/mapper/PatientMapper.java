package pl.baranowski.dev.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.entity.Patient;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PatientMapper {
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    PatientDTO toDto(Patient patient);

    @InheritInverseConfiguration
    Patient toEntity(PatientDTO patientDTO);
}
