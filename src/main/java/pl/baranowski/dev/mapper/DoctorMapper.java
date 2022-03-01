package pl.baranowski.dev.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.Doctor;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DoctorMapper {
    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    DoctorDTO toDto(Doctor doctor);

    @InheritInverseConfiguration
    @Mapping(target = "workingDays", ignore = true)
    @Mapping(target = "worksFromHour", ignore = true)
    @Mapping(target = "worksTillHour", ignore = true)
    @Mapping(target = "visits", ignore = true)
    Doctor toEntity(DoctorDTO doctorDTO);
}
