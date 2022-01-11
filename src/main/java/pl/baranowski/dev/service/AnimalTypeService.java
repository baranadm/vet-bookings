package pl.baranowski.dev.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.exception.AnimalTypeAllreadyExistsException;
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

	public AnimalTypeDTO addNew(AnimalTypeDTO dto) throws AnimalTypeAllreadyExistsException {
		AnimalType animalType = mapToEntity(dto);
		
		//if database contains this animalType, throw exception
		if(!findByName(animalType.getName()).isEmpty()) {
			throw new AnimalTypeAllreadyExistsException();
		}
		AnimalType result = animalTypeRepo.saveAndFlush(animalType);
		AnimalTypeDTO resultDTO = mapToDTO(result);
		return resultDTO;
	}

	public AnimalType findById(Long id) {
		return animalTypeRepo.findById(id).orElse(null);
	}
	
	public List<AnimalType> findByName(String name) {
		return animalTypeRepo.findByName(name);
	}

	private AnimalType mapToEntity(AnimalTypeDTO dto) {
		return modelMapper.map(dto, AnimalType.class);
	}
	
	private AnimalTypeDTO mapToDTO(AnimalType result) {
		return modelMapper.map(result, AnimalTypeDTO.class);
	}

}
