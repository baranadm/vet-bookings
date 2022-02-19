package pl.baranowski.dev.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.exception.DoubledSpecialtyException;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.NIPExistsException;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.service.DoctorService;

@RestController
@RequestMapping("/doctor")
public class DoctorController {
	
	public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 5);
	@Autowired
	private final DoctorService doctorService;

	public DoctorController(DoctorService doctorService) {
		this.doctorService = doctorService;
	}
	
	@GetMapping(value="/{id}", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody DoctorDTO getById(@PathVariable String id) throws NumberFormatException {
		return doctorService.getById(Long.decode(id));
	}
	
	@GetMapping(value="/", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Page<DoctorDTO> findAll(@RequestParam("page") String page, @RequestParam("size") String size) throws EmptyFieldException {
		// validation:
		if(page.isEmpty()) throw new EmptyFieldException("page");
		if(size.isEmpty()) throw new EmptyFieldException("size");
		Pageable requestedPageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
		Page<DoctorDTO> result = doctorService.findAll(requestedPageable);
		return result;
	}
	
	@PostMapping(value = "/", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody DoctorDTO addNew(@Valid @RequestBody DoctorDTO dto) throws NIPExistsException {
		// NIP validation  - custom validator
		return doctorService.addNew(dto);
	}
	
	@PutMapping("/fire/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void fire(@PathVariable("id") String id) throws NumberFormatException, DoctorNotActiveException {
		doctorService.fire(Long.decode(id));
	}
	
	@PutMapping(value = "{vetId}/addAnimalType/{atId}", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public void addAnimalType(@PathVariable String vetId, @PathVariable String atId) throws NumberFormatException, DoctorNotActiveException, DoubledSpecialtyException {
		doctorService.addAnimalType(Long.decode(vetId), Long.decode(atId));
	}
	
	@PutMapping(value = "{vetId}/addMedSpecialty/{msId}", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public void addMedSpecialty(@PathVariable String vetId, @PathVariable String msId) throws NumberFormatException, DoctorNotActiveException, DoubledSpecialtyException {
		doctorService.addMedSpecialty(Long.decode(vetId), Long.decode(msId));
	}
	
}
