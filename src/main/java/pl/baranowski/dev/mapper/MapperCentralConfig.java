package pl.baranowski.dev.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MapperCentralConfig {
}
