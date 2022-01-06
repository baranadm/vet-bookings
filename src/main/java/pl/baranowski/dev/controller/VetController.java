package pl.baranowski.dev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.service.VetService;

@Controller
public class VetController {
	
	@Autowired
	private final VetService vetService;

	public VetController(VetService vetService) {
		this.vetService = vetService;
	}
	
	@PostMapping(value = "/doctor")
	public @ResponseBody Vet add(@RequestParam("name") String name, @RequestParam("surname") String surname, @RequestParam("hourlyRate") String hourlyRate, @RequestParam("nip") String nip) {
		return vetService.put(new Vet(name, surname, Double.valueOf(hourlyRate), nip));
	}
	
}
