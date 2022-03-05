package pl.baranowski.dev.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.animalType.AnimalTypeAlreadyExistsException;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.service.AnimalTypeService;

@CrossOrigin
@RestController
@RequestMapping("/animalTypes")
public class AnimalTypeController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnimalTypeController.class);

	private final AnimalTypeService animalTypeService;

	public AnimalTypeController(AnimalTypeService animalTypeService) {
		this.animalTypeService = animalTypeService;
	}

	@GetMapping(value="/", produces="application/json;charset=UTF-8")
	public @ResponseBody List<AnimalTypeDTO> findAll() {
		LOGGER.info("Received GET request - / (findAll)");

		List<AnimalTypeDTO> animalTypeDTOs = animalTypeService.findAll();

		LOGGER.info("Returning response: animalTypeDTOs - size: {}",animalTypeDTOs.size());
		return animalTypeDTOs;
	}

	@GetMapping(value="/{id}", produces="application/json;charset=UTF-8")
	public @ResponseBody AnimalTypeDTO findById(@PathVariable String id) throws NotFoundException, InvalidParamException {
		LOGGER.info("Received GET request - /id with 'id'='{}'", id);

		AnimalTypeDTO animalTypeDTO = animalTypeService.findById(getIdFromString(id));

		LOGGER.info("Returning response: {}",animalTypeDTO);
		return animalTypeDTO;
	}

	private Long getIdFromString(String id) throws InvalidParamException {
		Long result;
		try {
			result = Long.decode(id);
		} catch (NumberFormatException e) {
			throw new InvalidParamException("id", id);
		}
		return result;
	}

	@GetMapping(value="/find", produces="application/json;charset=UTF-8")
	public @ResponseBody List<AnimalTypeDTO> findByName(@RequestParam("name") String name) throws EmptyFieldException {
		LOGGER.info("Received GET request - /find with 'name'='{}'", name);

		if(name.isEmpty()) {
			LOGGER.info("Param 'name' is empty, returning error.");

			throw new EmptyFieldException("name");
		}

		List<AnimalTypeDTO> animalTypeDTOs = animalTypeService.findByName(name);
		LOGGER.info("Returning response: list of animalTypeDTOs, size: {}",animalTypeDTOs.size());
		return animalTypeDTOs;
	}
	
	@PostMapping(value="/new", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody AnimalTypeDTO addNew(@Valid @RequestBody AnimalTypeDTO animalType) throws AnimalTypeAlreadyExistsException {
		LOGGER.info("Received POST request - /new with request body: {}", animalType);
		AnimalTypeDTO animalTypeDTO = animalTypeService.addNew(animalType);
		LOGGER.info("Creating new animalType success. Object created: {}", animalTypeDTO);
		return animalTypeDTO;
	}
}
