package pl.baranowski.dev.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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

import pl.baranowski.dev.dto.NewVisitDTO;
import pl.baranowski.dev.dto.SingleCheckResultDTO;
import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
import pl.baranowski.dev.exception.SearchRequestInvalidException;
import pl.baranowski.dev.exception.VetNotActiveException;
import pl.baranowski.dev.service.VetService;
import pl.baranowski.dev.service.VisitService;

@CrossOrigin
@RestController
@RequestMapping("/visit")
@Validated
public class VisitController {

	public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 5);
	@Autowired
	VisitService visitService;
	@Autowired
	VetService vetService;
	
	//TODO correct tests (was with @RequestBody)
	/*
	 * should produce result like: (array of JSON-serialized objects)
	 * [{
	 * 	"vet" : {
	 * 		"id": 12,
	 * 		"name": "Robert",
	 * 		"surname": "Kubica"
	 * 	}
	 * 	"epochFreeTimes": [1645072294, ....]
	 * }]
	 */
	@GetMapping(value="/check", produces = "application/json;charset=UTF-8")
	public @ResponseBody List<SingleCheckResultDTO> findFreeSlots(
			@RequestParam("animalTypeName") @NotBlank(message="Invalid search criteria: animalTypeName should not be empty.") String animalTypeName,
			@RequestParam("medSpecialtyName") @NotBlank(message="Invalid search criteria: medSpecialtyName should not be empty.") String medSpecialtyName,
			@RequestParam("epochStart") @NotBlank(message="Invalid search criteria: epochStart should not be empty.") @Pattern(regexp = "[0-9]+", message = "Invalid epoch format - only digits allowed") String epochStart,
			@RequestParam("epochEnd") @NotBlank(message="Invalid search criteria: epochEnd should not be empty.") @Pattern(regexp = "[0-9]+", message = "Invalid epoch format - only digits allowed") String epochEnd,
			@RequestParam("interval") @NotBlank(message="Invalid search criteria: interval should not be empty.")	@Pattern(regexp = "[0-9]+", message = "Invalid interval format - only digits allowed") String interval) throws SearchRequestInvalidException {
		return visitService.findFreeSlots(animalTypeName, medSpecialtyName, epochStart, epochEnd, interval);
	}
	
	@GetMapping(value="/{id}", produces="application/json;charset=UTF-8")
	public @ResponseBody VisitDTO getById(@PathVariable String id) throws NumberFormatException {
		return visitService.getById(Long.decode(id));
	}

	@GetMapping(value="/", produces="application/json;charset=UTF-8")
	public @ResponseBody Page<VisitDTO> findAll(
			@RequestParam("page") @Min(value=0, message="invalid parameter: page must be greather than or equal to 0") int page,
			@RequestParam("size") @Min(value=1, message="invalid parameter: size must be greather than or equal to 1") int size) {
		
		Pageable pageable = PageRequest.of(page, size);
		return visitService.findAll(pageable);
	}
	
	@PostMapping(value="/", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody VisitDTO addNew(@Valid @RequestBody NewVisitDTO nv) throws NewVisitNotPossibleException, VetNotActiveException {
		
		long vetId = Long.decode(nv.getVetId());
		long patientId = Long.decode(nv.getPatientId());
		long epoch = Long.decode(nv.getEpoch());
		
		return visitService.addNew(vetId, patientId, epoch);
	}
	
}
