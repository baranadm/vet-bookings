package pl.baranowski.dev.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.entity.Vet;

public class VetMapper extends ModelMapper {

	public VetMapper() {
		super();
		configureToDtoMap();
		configureToEntityMap();
	}
	
	// setting TypaMap to properly map ENTITY to DTO
	private void configureToDtoMap() {
		TypeMap<Vet, VetDTO> toDtoMap = this.createTypeMap(Vet.class, VetDTO.class);
		
		//config...
		toDtoMap.addMapping(Vet::getHourlyRate, VetDTO::setHourlyRateFromDouble);
	}

	// setting TypaMap to properly map DTO to ENTITY
	private void configureToEntityMap() {
		TypeMap<VetDTO, Vet> toEntityMap = this.createTypeMap(VetDTO.class, Vet.class);
		
		//config...
		toEntityMap.addMapping(VetDTO::getHourlyRate, Vet::setHourlyRateFromString);
	}
}
