package pl.baranowski.dev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.service.MedSpecialtyService;

@RestController
@RequestMapping("/medSpecialty")
public class MedSpecialtyController {

	@Autowired
	private final MedSpecialtyService medSpecialtyService;

	public MedSpecialtyController(MedSpecialtyService medSpecialtyService) {
		this.medSpecialtyService = medSpecialtyService;
	}

	@GetMapping("/all")
	public @ResponseBody List<MedSpecialty> findAll() {
		return medSpecialtyService.findAll();
	}

	@GetMapping("/{id}")
	public @ResponseBody List<MedSpecialty> getById(@PathVariable String id) {
		return medSpecialtyService.findAll();
	}
	
	@GetMapping("/get")
	public @ResponseBody List<MedSpecialty> findByName(@RequestParam("specialty") String specialty) {
		return medSpecialtyService.findByName(specialty);
	}
	
	@PostMapping("/new")
	public @ResponseBody MedSpecialty put(@RequestParam("specialty") String specialty) {
		return medSpecialtyService.addNew(new MedSpecialty(specialty));
	}
	
	
}
