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

import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.NIPExistsException;
import pl.baranowski.dev.service.VetService;

@RestController
@RequestMapping("/doctor")
public class VetController {
	
	public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 5);
	@Autowired
	private final VetService vetService;

	public VetController(VetService vetService) {
		this.vetService = vetService;
	}
	
	@GetMapping(value="/{id}", produces="application/json;charset=UTF-8")
	public @ResponseBody VetDTO getById(@PathVariable String id) throws NumberFormatException {
		return vetService.getById(Long.decode(id));
	}
	
	@GetMapping(value="/", produces="application/json;charset=UTF-8")
	public @ResponseBody Page<VetDTO> findAll(@RequestParam("page") String page, @RequestParam("size") String size) throws EmptyFieldException {
		// validation:
		if(page.isEmpty()) throw new EmptyFieldException("page");
		if(size.isEmpty()) throw new EmptyFieldException("size");
		Pageable requestedPageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
		Page<VetDTO> result = vetService.findAll(requestedPageable);
		return result;
	}
	
	@PostMapping(value = "/", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody VetDTO addNew(@Valid @RequestBody VetDTO dto) throws NIPExistsException {
		// TO ADD: validation of NIP
		return vetService.addNew(dto);
	}
	
	@PutMapping("/fire/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void fire(@PathVariable("id") String id) {
		vetService.fire(Long.decode(id));
	}
	
}
