package pl.baranowski.dev.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.doctor.DoctorAlreadyExistsException;
import pl.baranowski.dev.exception.doctor.DoctorDoubledSpecialtyException;
import pl.baranowski.dev.exception.doctor.DoctorNotActiveException;
import pl.baranowski.dev.service.DoctorService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Validated
@RestController
@RequestMapping("/doctors")
public class DoctorController {
    public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 5);
    private static final Logger LOGGER = LoggerFactory.getLogger(AnimalTypeController.class);
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    DoctorDTO getById(@PathVariable String id) throws NotFoundException, InvalidParamException {
        LOGGER.debug("Received request: @GET '/doctors/', method: getById(id='{}')", id);

        DoctorDTO doctorDTO = doctorService.getDTO(getIdFromString(id));

        LOGGER.debug("Returning response: {}", doctorDTO);
        return doctorDTO;
    }

    private Long getIdFromString(String stringId) throws InvalidParamException {
        try {
            return Long.decode(stringId);
        } catch (NumberFormatException ex) {
            throw new InvalidParamException("id", stringId);
        }
    }

    @GetMapping(value = "/", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Page<DoctorDTO> findAll(@Min(0) @NotBlank @RequestParam("page") String page,
                            @Min(1) @NotBlank @RequestParam("size") String size) throws InvalidParamException, EmptyFieldException {
        LOGGER.debug("Received request: @GET '/doctors/', method: findAll(page='{}', size ='{}')", page, size);

        Pageable requestedPageable = PageRequest.of(getIntegerFromString(page), getIntegerFromString(size));
        LOGGER.debug("Created pageable: {}", requestedPageable);

        Page<DoctorDTO> result = doctorService.findAll(requestedPageable);
        LOGGER.debug("Returning response: Page of DoctorDTOs - size: {}", result.getContent().size());
        return result;
    }

    private int getIntegerFromString(String str) throws InvalidParamException {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            throw new InvalidParamException("Invalid param: " + str);
        }
    }

    @PostMapping(value = "/", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    DoctorDTO addNew(@Valid @RequestBody DoctorDTO doctorDTO) throws DoctorAlreadyExistsException {
        LOGGER.debug("Received request: @POST '/doctors/', method: addNew(doctorDTO): {}", doctorDTO);

        DoctorDTO createdDoctorDTO = doctorService.addNew(doctorDTO);
        LOGGER.debug("Created new Doctor. Returning result: {}", createdDoctorDTO);
        return createdDoctorDTO;
    }

    @PutMapping("/fire/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    DoctorDTO fire(@PathVariable("id") String id) throws NotFoundException, DoctorNotActiveException, InvalidParamException {
        LOGGER.debug("Received request: @PUT '/doctors/', method: fire(id='{}')", id);

        DoctorDTO firedDoctorDTO = doctorService.fire(getIdFromString(id));
        LOGGER.info("Doctor has been fired. Returning result: {}", firedDoctorDTO);
        return firedDoctorDTO;
    }

    @PutMapping(value = "{doctorId}/addAnimalType/{atId}", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    DoctorDTO addAnimalType(@PathVariable String doctorId,
                            @PathVariable String atId) throws NotFoundException, DoctorDoubledSpecialtyException, DoctorNotActiveException, InvalidParamException {
        LOGGER.debug(
                "Received request: @PUT 'doctors/{doctorId}/addAnimalType/{animalTypeId} with doctorId='{}', animalTypeId='{}'",
                doctorId,
                atId);

        DoctorDTO updatedDoctorDTO = doctorService.addAnimalType(getIdFromString(doctorId), getIdFromString(atId));
        LOGGER.debug("AnimalType with id='{}' has been added to Doctor: {}. Returning DoctorDTO as a result.",
                     atId,
                     updatedDoctorDTO);
        return updatedDoctorDTO;
    }

    @PutMapping(value = "{doctorId}/addMedSpecialty/{msId}", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    DoctorDTO addMedSpecialty(@PathVariable String doctorId,
                              @PathVariable String msId) throws NotFoundException, DoctorDoubledSpecialtyException, DoctorNotActiveException, InvalidParamException {
        LOGGER.debug("Received request: @PUT 'doctors/{doctorId}/addMedSpecialty/{msId} with doctorId='{}', msId='{}'",
                     doctorId,
                     msId);

        DoctorDTO updatedDoctorDTO = doctorService.addMedSpecialty(getIdFromString(doctorId), getIdFromString(msId));
        LOGGER.debug("MedSpecialty with id='{}' has been added to Doctor: {}. Returning DoctorDTO as a result.",
                     msId,
                     updatedDoctorDTO);
        return updatedDoctorDTO;
    }

}