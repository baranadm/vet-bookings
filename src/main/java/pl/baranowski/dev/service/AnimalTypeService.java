package pl.baranowski.dev.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

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
	CustomMapper mapper;
	
	public AnimalTypeService(AnimalTypeRepository animalTypeRepo) {
		this.animalTypeRepo = animalTypeRepo;
	}

	public AnimalTypeDTO findById(Long id) throws EntityNotFoundException {
		AnimalType entry = animalTypeRepo.findById(id).orElseThrow(EntityNotFoundException::new);
		return mapper.toDto(entry);
	}

	public List<AnimalTypeDTO> findByName(String name) {
		return animalTypeRepo.findByName(name).stream().map(r -> mapper.toDto(r)).collect(Collectors.toList());
	}

	public List<AnimalTypeDTO> findAll() {
		return animalTypeRepo.findAll().stream().map(r -> mapper.toDto(r)).collect(Collectors.toList());
	}

	public AnimalTypeDTO addNew(AnimalTypeDTO dto) throws AnimalTypeAllreadyExistsException {
		AnimalType animalType = mapper.toEntity(dto);
		
		//if database contains this animalType, throw exception
		if(!findByName(animalType.getName()).isEmpty()) {
			throw new AnimalTypeAllreadyExistsException();
		}
		AnimalType result = animalTypeRepo.saveAndFlush(animalType);
		AnimalTypeDTO resultDTO = mapper.toDto(result);
		return resultDTO;
	}

}
