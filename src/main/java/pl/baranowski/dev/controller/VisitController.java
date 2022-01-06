package pl.baranowski.dev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.service.VisitService;

@RestController
public class VisitController {

	@Autowired
	VisitService visitService;
	
	@GetMapping(value = "/visit")
	public @ResponseBody List<Visit> findAll() {
		return visitService.findAll();
	}
	
	@PostMapping(value = "/visit")
	public @ResponseBody Visit put(@RequestParam("doctorId") String vetId, @RequestParam("patientId") String patientId, @RequestParam("dateTime") String dateTime) {
		return visitService.put(vetId, patientId, dateTime);
	}
}
