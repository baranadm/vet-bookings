package pl.baranowski.dev.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.repository.AnimalTypeRepository;

@Service
public class AnimalTypeService {

	@Autowired
	private final AnimalTypeRepository animalTypeRepo;
	
	@Autowired
	ModelMapper modelMapper;
	
	public AnimalTypeService(AnimalTypeRepository animalTypeRepo) {
		this.animalTypeRepo = animalTypeRepo;
	}

	public List<AnimalType> findAll() {
		return animalTypeRepo.findAll();
	}

	public AnimalTypeDTO addNew(AnimalTypeDTO dto) {
		AnimalType animalType = mapToEntity(dto);
		AnimalType result = animalTypeRepo.saveAndFlush(animalType);
		AnimalTypeDTO resultDTO = mapToDTO(result);
		return resultDTO;
	}

	public AnimalType findById(Long id) {
		return animalTypeRepo.findById(id).get();
	}
	
	public AnimalType findByName(String name) {
		return animalTypeRepo.findByName(name).get(0);
	}

	private AnimalType mapToEntity(AnimalTypeDTO dto) {
		return modelMapper.map(dto, AnimalType.class);
	}
	
	private AnimalTypeDTO mapToDTO(AnimalType result) {
		return modelMapper.map(result, AnimalTypeDTO.class);
	}

}
