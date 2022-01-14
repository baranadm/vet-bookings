package pl.baranowski.dev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.service.VetService;

@RestController
@RequestMapping("/doctor")
public class VetController {
	
	@Autowired
	private final VetService vetService;

	public VetController(VetService vetService) {
		this.vetService = vetService;
	}
	
	@GetMapping("/{id}")
	public @ResponseBody VetDTO getById(@PathVariable String id) throws NumberFormatException {
		return vetService.getById(Long.decode(id));
	}
	
	@PostMapping(value = "/doctor")
	public @ResponseBody Vet add(@RequestParam("name") String name, @RequestParam("surname") String surname, @RequestParam("hourlyRate") String hourlyRate, @RequestParam("nip") String nip) {
		return vetService.put(new Vet(name, surname, Double.valueOf(hourlyRate), nip));
	}
	
}
