package pl.baranowski.dev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.service.AnimalTypeService;
import pl.baranowski.dev.service.PatientService;

@RestController
@RequestMapping("patient")
public class PatientController {
	
	@Autowired
	PatientService patientService;
	
	@GetMapping("/all")
	public @ResponseBody List<Patient> findAll() {
		return patientService.findAll();
	}
	
	@PostMapping("/new")
	public @ResponseBody Patient put(@RequestParam("name") String name, @RequestParam("animalTypeName") String animalTypeName, @RequestParam("age") Integer age, @RequestParam("ownerName") String ownerName, @RequestParam("ownerEmail") String ownerEmail) {
		return patientService.put(name, animalTypeName, age, ownerName, ownerEmail);
	}
}
