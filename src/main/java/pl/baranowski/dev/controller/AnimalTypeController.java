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
	private final AnimalTypeService animalTypeService;

	public AnimalTypeController(AnimalTypeService service) {
		this.animalTypeService = service;
	}
	
	/** Unit tests:
	 * should return all AnimalType type data if data exists
	 * should return AnimalType empty list if no data exists
	 * should throw exception if service is null or blank
	 */
	@GetMapping("/all")
	public @ResponseBody List<AnimalType> findAll() {
		return animalTypeService.findAll();
	}
	
	@PostMapping("/new")
	public @ResponseBody AnimalType addNew(@Valid @RequestBody AnimalTypeDTO animalType) {
		return animalTypeService.addNew(animalType);
	}

	public AnimalType findById(long id) {
		return animalTypeService.findById(id);
	}

	public AnimalType findByName(String name) {
		return animalTypeService.findByName(name);
	}
	
}
