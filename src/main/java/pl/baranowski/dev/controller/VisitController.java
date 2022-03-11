package pl.baranowski.dev.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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

import pl.baranowski.dev.dto.DoctorsFreeSlotsDTO;
import pl.baranowski.dev.dto.NewVisitDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.epoch.InvalidEpochTimeException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.service.VisitService;

@CrossOrigin
@RestController
@RequestMapping("/visits")
@Validated
public class VisitController {

    public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 5);
    @Autowired
    VisitService visitService;

    @GetMapping(value = "/check", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    List<DoctorsFreeSlotsDTO> findFreeSlots(
            @RequestParam("animalTypeName") @NotBlank(message = "Invalid search criteria: animalTypeName should not be empty.") String animalTypeName,
            @RequestParam("medSpecialtyName") @NotBlank(message = "Invalid search criteria: medSpecialtyName should not be empty.") String medSpecialtyName,
            @RequestParam("epochStart") @NotBlank(message = "Invalid search criteria: epochStart should not be empty.") @Pattern(regexp = "[0-9]+", message = "Invalid epoch format - only digits allowed") String epochStart,
            @RequestParam("epochEnd") @NotBlank(message = "Invalid search criteria: epochEnd should not be empty.") @Pattern(regexp = "[0-9]+", message = "Invalid epoch format - only digits allowed") String epochEnd) throws InvalidEpochTimeException, NotFoundException, InvalidParamException {
        List<DoctorsFreeSlotsDTO> result = visitService.findAvailableVisits(animalTypeName,
                                                                            medSpecialtyName,
                                                                            decodeEpoch(epochStart),
                                                                            decodeEpoch(epochEnd));
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
        return visitService.getById(Long.decode(id));
    }

    @GetMapping(value = "/", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Page<VisitDTO> findAll(
            @RequestParam("page") @Pattern(regexp = "\\d+", message = "Parameter [page] must be natural number, greater than or equal to 0.") String page,
            @RequestParam("size") @Pattern(regexp = "\\d+", message = "Parameter [size] must be natural number.") @Min(value = 1, message = "Parameter [size] must be greater than or equal to 1") String size) {

        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        return visitService.findAll(pageable);
    }

    @PostMapping(value = "/", produces = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    // , binding result
    // TODO dodać loggera
    public @ResponseBody
    VisitDTO addNew(@Valid @RequestBody NewVisitDTO nv) throws Exception {

        long doctorId = Long.decode(nv.getDoctorId());
        long patientId = Long.decode(nv.getPatientId());
        long epoch = Long.decode(nv.getEpoch());

        // TODO fasada/manager
        return visitService.addNew(doctorId, patientId, epoch);
    }

}
