package pl.baranowski.dev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.service.AnimalTypeService;

@RestController
@RequestMapping("/animalTypes")
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
	
	@PutMapping("/new")
	public @ResponseBody AnimalType put(@RequestParam("name") String name) {
		return service.put(new AnimalType(name));
	}
	
}
