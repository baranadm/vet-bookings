package pl.baranowski.dev.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.medSpecialty.MedSpecialtyAlreadyExistsException;
import pl.baranowski.dev.service.MedSpecialtyService;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@CrossOrigin
@RestController
@RequestMapping("/medSpecialties")
public class MedSpecialtyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MedSpecialtyController.class);

    private final MedSpecialtyService medSpecialtyService;

    public MedSpecialtyController(MedSpecialtyService medSpecialtyService) {
        this.medSpecialtyService = medSpecialtyService;
    }

    @GetMapping(value = "/all", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    List<MedSpecialtyDTO> findAll() {
        LOGGER.debug("Received request: @GET '/medSpecialties/', method: findAll()");
        List<MedSpecialtyDTO> result = medSpecialtyService.findAll();
        LOGGER.debug("Returning response: MedSpecialties DTO list - size: {}", result.size());
        return result;
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    MedSpecialtyDTO getById(@PathVariable String id) throws InvalidParamException, NotFoundException {
        LOGGER.debug("Received request: @GET '/medSpecialties/{id}', method: getById(id='{}')", id);
        MedSpecialtyDTO result = medSpecialtyService.getById(getIdFromString(id));
        LOGGER.debug("Returning response: {}", result);
        return result;
    }

    private Long getIdFromString(String idAsString) throws InvalidParamException {
        try {
            return Long.decode(idAsString);
        } catch (NumberFormatException e) {
            throw new InvalidParamException("id", idAsString);
        }
    }

    @GetMapping(value = "/find", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    MedSpecialtyDTO findByName(@NotBlank(message = "specialty must not be null or empty") @RequestParam("specialty") String specialtyName) throws NotFoundException {
        LOGGER.debug("Received request: @GET '/medSpecialties/find', method: findByName(name`='{}')", specialtyName);
        MedSpecialtyDTO result = medSpecialtyService.findByName(specialtyName);
        LOGGER.debug("Returning response: {}", result);
        return result;
    }

    @PostMapping(value = "/new", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    MedSpecialtyDTO addNew(@NotBlank(message = "specialty must not be null or empty") @RequestParam("specialty") String specialtyName) throws MedSpecialtyAlreadyExistsException {
        LOGGER.debug("Received request: @POST '/medSpecialties/new', method: addNew(name='{}')", specialtyName);
        MedSpecialtyDTO result = medSpecialtyService.addNew(specialtyName);
        LOGGER.debug("Creating new MedSpecialty success. Object created: {}", result);
        return result;
    }

}
