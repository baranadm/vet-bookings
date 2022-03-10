package pl.baranowski.dev.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/medSpecialty")
public class MedSpecialtyController {

    @Autowired
    MedSpecialtyService medSpecialtyService;

    @GetMapping(value = "/all", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    List<MedSpecialtyDTO> findAll() {
        return medSpecialtyService.findAll();
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    MedSpecialtyDTO getById(@PathVariable String id) throws InvalidParamException, NotFoundException {
        return medSpecialtyService.getById(getIdFromString(id));
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
        return medSpecialtyService.findByName(specialtyName);
    }

    @PostMapping(value = "/new", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    MedSpecialtyDTO addNew(@NotBlank(message = "specialty must not be null or empty") @RequestParam("specialty") String specialtyName) throws MedSpecialtyAlreadyExistsException {
        return medSpecialtyService.addNew(specialtyName);
    }

}
