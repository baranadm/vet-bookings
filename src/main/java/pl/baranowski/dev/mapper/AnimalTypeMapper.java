package pl.baranowski.dev.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.entity.AnimalType;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AnimalTypeMapper {
    AnimalTypeMapper INSTANCE = Mappers.getMapper(AnimalTypeMapper.class);

    AnimalTypeDTO toDto(AnimalType animalType);

    @InheritInverseConfiguration
    AnimalType toEntity(AnimalTypeDTO animalTypeDTO);
}
