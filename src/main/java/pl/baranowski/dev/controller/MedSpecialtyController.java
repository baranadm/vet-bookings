package pl.baranowski.dev.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.medSpecialty.MedSpecialtyAlreadyExistsException;
import pl.baranowski.dev.service.MedSpecialtyService;

@CrossOrigin
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
	public @ResponseBody MedSpecialtyDTO getById(@PathVariable String id) throws InvalidParamException, NotFoundException {
		return medSpecialtyService.getById(getIdFromString(id));
	}

	private Long getIdFromString(String idAsString) throws InvalidParamException {
		try {
			return Long.decode(idAsString);
		} catch(NumberFormatException e) {
			throw new InvalidParamException("id", idAsString);
		}
	}

	@GetMapping(value="/find", produces="application/json;charset=UTF-8")
	public @ResponseBody List<MedSpecialtyDTO> findByName(@RequestParam("specialty") String name) throws EmptyFieldException {
		if(name.isEmpty()) {
			throw new EmptyFieldException("specialty");
		}
		return medSpecialtyService.findByName(name);
	}
	
	@PostMapping(value="/new", consumes="application/json;charset=UTF-8", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody MedSpecialtyDTO addNew(@Valid @RequestBody MedSpecialtyDTO dto) throws MedSpecialtyAlreadyExistsException, EmptyFieldException {
		return medSpecialtyService.addNew(dto);
	}
	
}
