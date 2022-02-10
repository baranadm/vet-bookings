package pl.baranowski.dev.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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

import pl.baranowski.dev.dto.NewVisitDTO;
import pl.baranowski.dev.dto.SearchRequestDTO;
import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
import pl.baranowski.dev.exception.SearchRequestInvalidException;
import pl.baranowski.dev.exception.VetNotActiveException;
import pl.baranowski.dev.service.VetService;
import pl.baranowski.dev.service.VisitService;

@RestController
@RequestMapping("/visit")
@Validated
public class VisitController {

	public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 5);
	@Autowired
	VisitService visitService;
	@Autowired
	VetService vetService;
	
	//TODO checking free visits feature
	@GetMapping(value="/check", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	public @ResponseBody Map<VetDTO, List<Long>> findFreeSlots(@Valid @RequestBody SearchRequestDTO requestBody) throws SearchRequestInvalidException {
		return visitService.findFreeSlots(requestBody);
	}
	
	@GetMapping(value="/{id}", produces="application/json;charset=UTF-8")
	public @ResponseBody VisitDTO getById(@PathVariable String id) throws NumberFormatException {
		return visitService.getById(Long.decode(id));
	}
	
	// TODO change @ReqestParam page and size to @RequestBody PabeableDTO
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
