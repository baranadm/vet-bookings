package pl.baranowski.dev.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.dto.NewPatientDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.exception.PatientAllreadyExistsException;
import pl.baranowski.dev.service.PatientService;

@RestController
@RequestMapping("patient")
public class PatientController {
	
	@Autowired
	PatientService patientService;
	
	@GetMapping(value="/all", produces = "application/json;charset=UTF-8")
	public @ResponseBody List<PatientDTO> findAll() {
		return patientService.findAll();
	}
	
	@PostMapping(value="/", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody PatientDTO addNew(@Valid @RequestBody NewPatientDTO newPatient) throws PatientAllreadyExistsException {
		return patientService.addNew(newPatient);
	}
}
