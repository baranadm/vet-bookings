package pl.baranowski.dev.controller;

import javax.validation.Valid;
import javax.validation.constraints.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.dto.NewPatientDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.patient.PatientAlreadyExistsException;
import pl.baranowski.dev.service.PatientService;

@Validated
@RestController
@RequestMapping("/patients")
public class PatientController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    PatientDTO getById(@PathVariable("id") @NotEmpty @Min(1) String id) throws NotFoundException {
        LOGGER.debug("Received request: @GET '/patients/{id}, id='{}'", id);

        PatientDTO resultDTO = patientService.getDto(Long.decode(id));
        LOGGER.debug("Returning DTO result: {}", resultDTO);
        return resultDTO;
    }

    @GetMapping(value = "/", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Page<PatientDTO> findAll(@NotBlank @Min(0) @RequestParam("page") String page,
                             @NotBlank @Min(1) @RequestParam("size") String size) throws InvalidParamException {
        LOGGER.debug("Received request: @GET '/patients/, page='{}', size='{}'", page, size);

        Pageable requestedPageable = PageRequest.of(getIntFromString(page), getIntFromString(size));
        LOGGER.debug("Pageable created: {}", requestedPageable);

        Page<PatientDTO> result = patientService.findAll(requestedPageable);
        LOGGER.debug("Returning Page with {} elements.", result.getSize());
        return result;
    }

    private int getIntFromString(String intValue) throws InvalidParamException {
        try {
            return Integer.parseInt(intValue);
        } catch (NumberFormatException e) {
            throw new InvalidParamException("id", intValue);
        }
    }

    @PostMapping(value = "/", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    PatientDTO addNew(@Valid @RequestBody NewPatientDTO newPatient) throws PatientAlreadyExistsException, NotFoundException {
        LOGGER.debug("Received request: @POST '/patients/, newPatient={}", newPatient);

        PatientDTO patientDTO = patientService.addNew(newPatient);
        LOGGER.debug("Patient created, returning DTO result: {}", patientDTO);
        return patientDTO;
    }
}