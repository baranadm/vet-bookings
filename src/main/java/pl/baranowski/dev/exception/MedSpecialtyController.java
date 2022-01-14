package pl.baranowski.dev.exception;

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

import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.service.MedSpecialtyService;

@RestController
@RequestMapping("/medSpecialty")
public class MedSpecialtyController {

	@Autowired
	MedSpecialtyService medSpecialtyService;

	@GetMapping(value="/all", produces="application/json;charset=UTF-8")
	public @ResponseBody List<MedSpecialtyDTO> findAll() {
		return medSpecialtyService.findAll();
	}

	@GetMapping(value="/{id}", produces="application/json;charset=UTF-8")
	public @ResponseBody MedSpecialtyDTO getById(@PathVariable String id) {
		return medSpecialtyService.getById(Long.decode(id));
	}
	
	@GetMapping(value="/find", produces="application/json;charset=UTF-8")
	public @ResponseBody List<MedSpecialtyDTO> findByName(@RequestParam("specialty") String name) throws EmptyFieldException {
		if(name.isEmpty()) {
			throw new EmptyFieldException("specialty");
		}
		return medSpecialtyService.findByName(name);
	}
	
	@PostMapping(value="/new", consumes="application/json;charset=UTF-8", produces="application/json;charset=UTF-8")
	public @ResponseBody MedSpecialtyDTO addNew(@Valid @RequestBody MedSpecialtyDTO dto) throws MedSpecialtyAllreadyExistsException, EmptyFieldException {
		return medSpecialtyService.addNew(dto);
	}
	
	
}
