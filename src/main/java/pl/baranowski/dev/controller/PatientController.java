package pl.baranowski.dev.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.patient.PatientAlreadyExistsException;
import pl.baranowski.dev.service.PatientService;

@RestController
@RequestMapping("/patient")
public class PatientController {
	
	@Autowired
	PatientService patientService;
	
	@GetMapping(value="/{id}", produces="application/json;charset=UTF-8")
	public @ResponseBody PatientDTO getById(@PathVariable String id) {
		return patientService.getDto(Long.decode(id));
	}
	
	@GetMapping(value="/", produces = "application/json;charset=UTF-8")
	public @ResponseBody Page<PatientDTO> findAll(@RequestParam("page") String page, @RequestParam("size") String size) throws EmptyFieldException {
		// validation:
		if(page.isEmpty()) throw new EmptyFieldException("page");
		if(size.isEmpty()) throw new EmptyFieldException("size");
		Pageable requestedPageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
		return patientService.findAll(requestedPageable);
	}
	
	@PostMapping(value="/", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody PatientDTO addNew(@Valid @RequestBody NewPatientDTO newPatient) throws PatientAlreadyExistsException, NotFoundException {
		return patientService.addNew(newPatient);
	}
}
