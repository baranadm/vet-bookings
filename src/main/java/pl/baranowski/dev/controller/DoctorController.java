package pl.baranowski.dev.controller;

import javax.print.Doc;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pl.baranowski.dev.exception.*;
import pl.baranowski.dev.service.DoctorService;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
	public static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 5);
	private static final Logger LOGGER = LoggerFactory.getLogger(AnimalTypeController.class);


	private final DoctorService doctorService;

	public DoctorController(DoctorService doctorService) {
		this.doctorService = doctorService;
	}

	@GetMapping(value="/{id}", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody DoctorDTO getById(@PathVariable String id) throws NotFoundException, BadRequestException {
		LOGGER.info("Received GET request - /id with 'id'='{}'", id);

		DoctorDTO doctorDTO = doctorService.getDto(getIdFromString(id));

		LOGGER.info("Returning response: {}",doctorDTO);
		return doctorDTO;
	}

	private Long getIdFromString(String stringId) throws BadRequestException {
		Long id;
		try {
			id = Long.decode(stringId);
		} catch(NumberFormatException ex) {
			throw new BadRequestException("Invalid id: " + stringId);
		}
		return id;
	}

	@GetMapping(value="/", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Page<DoctorDTO> findAll(@Min(0) @RequestParam("page") String page, @RequestParam("size") String size) throws BadRequestException {
		LOGGER.info("Received GET request - / (findAll) with params: page='{}', size='{}'", page, size);

		validatePageAndSize(page, size);
		LOGGER.info("Params are valid, creating pageable...");

		Pageable requestedPageable = PageRequest.of(getIntegerFromString(page), getIntegerFromString(size));
		Page<DoctorDTO> result = doctorService.findAll(requestedPageable);

		LOGGER.info("Returning response: Page of DoctorDTOs - size: {}",result.getContent().size());
		return result;
	}

	private int getIntegerFromString(String str) throws BadRequestException {
		Integer result;
		try {
			result = Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			throw new BadRequestException("Invalid param: " + str);
		}
		return result;
	}

	private void validatePageAndSize(String page, String size) throws BadRequestException {
		LOGGER.info("Validating params: page='{}', size='{}'", page, size);
		if(page.isEmpty()) throw new BadRequestException("page");
		if(size.isEmpty()) throw new BadRequestException("size");
	}

	@PostMapping(value = "/", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody DoctorDTO addNew(@Valid @RequestBody DoctorDTO doctorDTO) throws ForbiddenException {
		LOGGER.info("Received POST request - / (addNew) with request body: {}", doctorDTO);

		DoctorDTO createdDoctorDTO = doctorService.addNew(doctorDTO);

		LOGGER.info("Creating new animalType success. Object created: {}", createdDoctorDTO);
		return createdDoctorDTO;
	}
	
	@PutMapping("/fire/{id}")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody DoctorDTO fire(@PathVariable("id") String id) throws BadRequestException, ForbiddenException, NotFoundException {
		LOGGER.info("Received PUT request - /fire/id with id='{}'", id);

		DoctorDTO firedDoctorDTO = doctorService.fire(getIdFromString(id));

		LOGGER.info("Doctor has been fired. Fired Doctor: {}", firedDoctorDTO);
		return firedDoctorDTO;
	}
	
	@PutMapping(value = "{doctorId}/addAnimalType/{atId}", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody DoctorDTO addAnimalType(@PathVariable String doctorId, @PathVariable String atId) throws BadRequestException, ForbiddenException, NotFoundException {
		LOGGER.info("Received PUT request - /doctorId/addAnimalType/animalTypeId with doctorId='{}', animalTypeId='{}'", doctorId, atId);

		DoctorDTO updatedDoctorDTO = doctorService.addAnimalType(getIdFromString(doctorId), getIdFromString(atId));

		LOGGER.info("AnimalType with id='{}' has been added to Doctor: {}", atId, updatedDoctorDTO);
		return updatedDoctorDTO;
	}
	
	@PutMapping(value = "{doctorId}/addMedSpecialty/{msId}", produces="application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody DoctorDTO addMedSpecialty(@PathVariable String doctorId, @PathVariable String msId) throws BadRequestException, ForbiddenException, NotFoundException {
		LOGGER.info("Received PUT request - /doctorId/addMedSpecialty/medSpecialtyId with doctorId='{}', medSpecialtyId='{}'", doctorId, msId);

		DoctorDTO updatedDoctorDTO = doctorService.addMedSpecialty(getIdFromString(doctorId), getIdFromString(msId));

		LOGGER.info("MedSpecialty with id='{}' has been added to Doctor: {}", msId, updatedDoctorDTO);
		return updatedDoctorDTO;
	}
	
}