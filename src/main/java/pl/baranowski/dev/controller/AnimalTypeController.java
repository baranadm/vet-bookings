package pl.baranowski.dev.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.animalType.AnimalTypeAlreadyExistsException;
import pl.baranowski.dev.service.AnimalTypeService;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@CrossOrigin
@RestController
@RequestMapping("/animalTypes")
public class AnimalTypeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnimalTypeController.class);

    private final AnimalTypeService animalTypeService;

    public AnimalTypeController(AnimalTypeService animalTypeService) {
        this.animalTypeService = animalTypeService;
    }

    @GetMapping(value = "/", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    List<AnimalTypeDTO> findAll() {
        LOGGER.info("Received GET request - / (findAll)");

        List<AnimalTypeDTO> animalTypeDTOs = animalTypeService.findAll();

        LOGGER.info("Returning response: animalTypeDTOs - size: {}", animalTypeDTOs.size());
        return animalTypeDTOs;
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    AnimalTypeDTO findById(@PathVariable String id) throws NotFoundException, InvalidParamException {
        LOGGER.info("Received GET request - /id with 'id'='{}'", id);

        AnimalTypeDTO animalTypeDTO = animalTypeService.findById(getIdFromString(id));

        LOGGER.info("Returning response: {}", animalTypeDTO);
        return animalTypeDTO;
    }

    private Long getIdFromString(String id) throws InvalidParamException {
        try {
            return Long.decode(id);
        } catch (NumberFormatException e) {
            throw new InvalidParamException("id", id);
        }
    }

    @GetMapping(value = "/find", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    AnimalTypeDTO findByName(@NotBlank(message = "name must not be null or empty") @RequestParam("name") String name) throws NotFoundException {
        LOGGER.info("Received GET request - /find with 'name'='{}'", name);
        AnimalTypeDTO animalTypeDTO = animalTypeService.findByName(name);
        LOGGER.info("Returning response: {}", animalTypeDTO);
        return animalTypeDTO;
    }

    @PostMapping(value = "/new", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    AnimalTypeDTO addNew(@NotBlank(message = "name must not be null or empty") @RequestParam("name") String name) throws AnimalTypeAlreadyExistsException {
        LOGGER.info("Received POST request - /new with name: {}", name);
        AnimalTypeDTO animalTypeDTO = animalTypeService.addNew(name);
        LOGGER.info("Creating new animalType success. Object created: {}", animalTypeDTO);
        return animalTypeDTO;
    }
}
