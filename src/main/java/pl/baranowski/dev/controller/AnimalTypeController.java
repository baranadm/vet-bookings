package pl.baranowski.dev.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.exception.AnimalTypeAllreadyExistsException;
import pl.baranowski.dev.service.AnimalTypeService;

@RestController
@RequestMapping("/animalType")
public class AnimalTypeController {

	@Autowired
	AnimalTypeService animalTypeService;
	
	@GetMapping(value="/all", produces="application/json;charset=UTF-8")
	public @ResponseBody List<AnimalTypeDTO> findAll() {
		return animalTypeService.findAll();
	}

	@GetMapping(value="/{id}", produces="application/json;charset=UTF-8")
	public @ResponseBody AnimalTypeDTO findById(@PathVariable String id) throws NumberFormatException {
		return animalTypeService.findById(Long.decode(id));
	}

	@GetMapping(value="/find", produces="application/json;charset=UTF-8")
	public @ResponseBody List<AnimalTypeDTO> findByName(@RequestParam("name") String name) {
		return animalTypeService.findByName(name);
	}
	
	@PostMapping(value="/new", produces="application/json;charset=UTF-8")
	public @ResponseBody AnimalTypeDTO addNew(@Valid @RequestBody AnimalTypeDTO animalType) throws AnimalTypeAllreadyExistsException {
		return animalTypeService.addNew(animalType);
	}
	
}
