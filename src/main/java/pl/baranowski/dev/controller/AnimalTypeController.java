package pl.baranowski.dev.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.service.AnimalTypeService;

@RestController
@RequestMapping("/animalType")
public class AnimalTypeController {

	@Autowired
	private final AnimalTypeService service;

	public AnimalTypeController(AnimalTypeService service) {
		this.service = service;
	}
	
	@GetMapping("/all")
	public @ResponseBody List<AnimalType> findAll() {
		return service.findAll();
	}
	
	@PostMapping("/new")
	public @ResponseBody AnimalType put(@Valid @RequestBody AnimalTypeDTO animalType) {
		return service.addNew(animalType);
	}
	
}
