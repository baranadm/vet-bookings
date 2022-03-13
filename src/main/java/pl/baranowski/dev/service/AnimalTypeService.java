package pl.baranowski.dev.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.animalType.AnimalTypeAlreadyExistsException;
import pl.baranowski.dev.mapper.AnimalTypeMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalTypeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnimalTypeService.class);

    private final AnimalTypeRepository animalTypeRepo;
    private final AnimalTypeMapper mapper;

    public AnimalTypeService(AnimalTypeRepository animalTypeRepo, AnimalTypeMapper mapper) {
        this.animalTypeRepo = animalTypeRepo;
        this.mapper = mapper;
    }

    public AnimalTypeDTO findById(Long id) throws NotFoundException {
        LOGGER.debug("findById(id='{}')", id);

        AnimalType result = animalTypeRepo.findById(id).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Animal type with id=" + id + " has not been found.");
            LOGGER.error(e.getMessage(), e);
            return e;
        });
        LOGGER.debug("Animal type found: {}", result);

        AnimalTypeDTO resultDTO = mapper.toDto(result);
        LOGGER.debug("Returning mapped result: {}", resultDTO);
        return resultDTO;
    }

    public AnimalTypeDTO findByName(String name) throws NotFoundException {
        LOGGER.debug("findByName(name='{}')", name);

        AnimalType result = animalTypeRepo.findOneByName(name).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Animal type with name=" + name + " has not been found.");
            LOGGER.error(e.getMessage(), e);
            return e;
        });
        LOGGER.debug("Animal type found: {}", result);

        AnimalTypeDTO resultDTO = mapper.toDto(result);
        LOGGER.debug("Returning mapped result: {}", resultDTO);
        return mapper.toDto(result);
    }

    public List<AnimalTypeDTO> findAll() {
        LOGGER.debug("findAll()");
        List<AnimalTypeDTO> result = animalTypeRepo.findAll()
                                                   .stream()
                                                   .map(mapper::toDto)
                                                   .collect(Collectors.toList());
        LOGGER.debug("Animal Types found: {}, returning result.", result.size());
        return result;
    }

    public AnimalTypeDTO addNew(String name) throws AnimalTypeAlreadyExistsException {
        LOGGER.debug("addNew(name='{}')", name);
        try {
            AnimalType result = animalTypeRepo.save(new AnimalType(name));
            LOGGER.debug("New Animal Type saved, result: {}", result);
            AnimalTypeDTO resultDTO = mapper.toDto(result);
            LOGGER.debug("Mapping and returning DTO: {}", resultDTO);
            return resultDTO;
        } catch (DataIntegrityViolationException e) {
            LOGGER.error(e.getMessage(), e);
            AnimalTypeAlreadyExistsException animalTypeException = new AnimalTypeAlreadyExistsException(name);
            LOGGER.error("Throwing ApiException: {}", animalTypeException.getMessage(), animalTypeException);
            throw animalTypeException;
        }
    }

}