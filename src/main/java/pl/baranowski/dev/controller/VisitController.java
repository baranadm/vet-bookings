package pl.baranowski.dev.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.baranowski.dev.dto.DoctorsFreeSlotsDTO;
import pl.baranowski.dev.dto.NewVisitDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.epoch.InvalidEpochTimeException;
import pl.baranowski.dev.service.VisitService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/visits")
@Validated
public class VisitController {
    public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 5);
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitController.class);
    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping(value = "/check", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    List<DoctorsFreeSlotsDTO> findFreeSlots(
            @RequestParam("animalTypeName") @NotBlank(message = "Invalid search criteria: animalTypeName should not be empty.") String animalTypeName,
            @RequestParam("medSpecialtyName") @NotBlank(message = "Invalid search criteria: medSpecialtyName should not be empty.") String medSpecialtyName,
            @RequestParam("epochStart") @NotBlank(message = "Invalid search criteria: epochStart should not be empty.") @Pattern(regexp = "[0-9]+", message = "Invalid epoch format - only digits allowed") String epochStart,
            @RequestParam("epochEnd") @NotBlank(message = "Invalid search criteria: epochEnd should not be empty.") @Pattern(regexp = "[0-9]+", message = "Invalid epoch format - only digits allowed") String epochEnd) throws InvalidEpochTimeException, NotFoundException, InvalidParamException {
        LOGGER.debug(
                "Received request: @GET '/visits/check', method: findFreeSlots(animalTypeName='{}', medSpecialtyName='{}', epochStart='{}', epochEnd='{}')",
                animalTypeName,
                medSpecialtyName,
                epochStart,
                epochEnd);

        List<DoctorsFreeSlotsDTO> result = visitService.findAvailableVisits(animalTypeName,
                                                                            medSpecialtyName,
                                                                            decodeEpoch(epochStart),
                                                                            decodeEpoch(epochEnd));
        LOGGER.debug("Found {} free slots. Returning result.", result.size());
        return result;
    }

    private Long decodeEpoch(String epoch) throws InvalidParamException {
        try {
            return Long.decode(epoch);
        } catch (NumberFormatException e) {
            throw new InvalidParamException("Invalid epoch. Cannot convert to number value.");
        }
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    VisitDTO getById(@Pattern(regexp = "\\d+", message = "Parameter [size] must be natural number.") @Min(1) @PathVariable String id) throws NumberFormatException, NotFoundException {
        LOGGER.debug("Received request: @GET '/visits/{id}', method: getById(id='{}')", id);

        VisitDTO result = visitService.getById(Long.decode(id));
        LOGGER.debug("Visit found: {}", result);
        return result;
    }

    @GetMapping(value = "/", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Page<VisitDTO> findAll(
            @RequestParam("page") @Pattern(regexp = "\\d+", message = "Parameter [page] must be natural number, greater than or equal to 0.") String page,
            @RequestParam("size") @Pattern(regexp = "\\d+", message = "Parameter [size] must be natural number.") @Min(value = 1, message = "Parameter [size] must be greater than or equal to 1") String size) {
        LOGGER.debug("Received request: @GET '/visits/', method: findAll(page='{}', size='{}')", page, size);

        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        LOGGER.debug("Created pageable: {}", pageable);

        Page<VisitDTO> result = visitService.findAll(pageable);
        LOGGER.debug("Returning result: {}", result);
        return result;
    }

    @PostMapping(value = "/", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    VisitDTO addNew(@Valid @RequestBody NewVisitDTO nv) throws Exception {
        LOGGER.debug("Received request: @POST '/visits/', method: addNew(NewVisitDTO): {}", nv);

        long doctorId = Long.decode(nv.getDoctorId());
        long patientId = Long.decode(nv.getPatientId());
        long epoch = Long.decode(nv.getEpoch());
        LOGGER.debug("Creating new Visit for arguments: doctorId='{}', patientId='{}', epoch='{}'.",
                     doctorId,
                     patientId,
                     epoch);

        VisitDTO result = visitService.addNew(doctorId, patientId, epoch);
        LOGGER.debug("Returning result: {}", result);
        return result;
    }

}
