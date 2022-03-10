package pl.baranowski.dev.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

	public AnimalTypeDTO findByName(String name) throws NotFoundException {
		AnimalType result = animalTypeRepo.findOneByName(name).orElseThrow(() -> new NotFoundException("Animal type with name=" + name + " has not been found."));
		return mapper.toDto(result);
	}

	public List<AnimalTypeDTO> findAll() {
		return animalTypeRepo.findAll().stream().map(r -> mapper.toDto(r)).collect(Collectors.toList());
	}

	public AnimalTypeDTO addNew(String name) throws AnimalTypeAlreadyExistsException {
		try {
			AnimalType result = animalTypeRepo.save(new AnimalType(name));
			return mapper.toDto(result);
		} catch(DataIntegrityViolationException e) {
			throw new AnimalTypeAlreadyExistsException(name);
		}
	}

}
