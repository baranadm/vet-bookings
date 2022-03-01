package pl.baranowski.dev.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.MedSpecialty;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MedSpecialtyMapper {
    MedSpecialtyMapper INSTANCE = Mappers.getMapper(MedSpecialtyMapper.class);

    MedSpecialtyDTO toDto(MedSpecialty medSpecialty);

    @InheritInverseConfiguration
    MedSpecialty toEntity(MedSpecialtyDTO medSpecialtyDTO);
}
