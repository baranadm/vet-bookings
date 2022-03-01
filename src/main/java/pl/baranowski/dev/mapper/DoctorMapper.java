package pl.baranowski.dev.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.Doctor;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DoctorMapper {
    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    DoctorDTO toDto(Doctor doctor);

    @InheritInverseConfiguration
    Doctor toEntity(DoctorDTO doctorDTO);
}
