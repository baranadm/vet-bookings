package pl.baranowski.dev.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.animalType.AnimalTypeAlreadyExistsException;
import pl.baranowski.dev.mapper.AnimalTypeMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;

@Service
public class AnimalTypeService {

	private final AnimalTypeRepository animalTypeRepo;
	
	@Autowired
	AnimalTypeMapper mapper;
	
	public AnimalTypeService(AnimalTypeRepository animalTypeRepo) {
		this.animalTypeRepo = animalTypeRepo;
	}

	public AnimalTypeDTO findById(Long id) throws NotFoundException {
		AnimalType entry = animalTypeRepo.findById(id).orElseThrow(() -> new NotFoundException("Animal type with id=" + id + " has not been found."));
		return mapper.toDto(entry);
	}

	public List<AnimalTypeDTO> findByName(String name) {
		return animalTypeRepo.findByName(name).stream().map(r -> mapper.toDto(r)).collect(Collectors.toList());
	}

	public List<AnimalTypeDTO> findAll() {
		return animalTypeRepo.findAll().stream().map(r -> mapper.toDto(r)).collect(Collectors.toList());
	}

	public AnimalTypeDTO addNew(AnimalTypeDTO dto) throws AnimalTypeAlreadyExistsException {
		if(animalTypeRepo.findOneByName(dto.getName()).isPresent()) {
			throw new AnimalTypeAlreadyExistsException(dto.getName());
		}
		AnimalType newAnimalType = mapper.toEntity(dto);
		AnimalTypeDTO newAnimalTypeDTO = mapper.toDto(animalTypeRepo.save(newAnimalType));
		return newAnimalTypeDTO;
	}

}
