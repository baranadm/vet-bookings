package pl.baranowski.dev.service;

import java.util.Arrays;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.NewPatientDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.patient.PatientAlreadyExistsException;
import pl.baranowski.dev.mapper.PatientMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.PatientRepository;

@Service
public class PatientService {
	
	@Autowired
	PatientRepository patientRepo;
	@Autowired
	AnimalTypeRepository animalTypeRepo;
	@Autowired
	PatientMapper mapper;
	
	public Page<PatientDTO> findAll(Pageable pageable) {
		Page<Patient> result = patientRepo.findAll(pageable);
		return result.map(entity -> mapper.toDto(entity));
	}
	
	public PatientDTO getDto(Long patientId) {
		Patient result = get(patientId);
		return mapper.toDto(result);
	}

	public Patient get(Long patientId) {
		Patient result = patientRepo.findById(patientId).orElseThrow(() -> new EntityNotFoundException("Patient with id " + patientId + " has not been found."));
		return result;
	}

	public PatientDTO addNew(NewPatientDTO newPatientDTO) throws PatientAlreadyExistsException, NotFoundException {
		AnimalType animalType = findAnimalType(newPatientDTO.getAnimalTypeName());

		Patient patient = new Patient(newPatientDTO.getName(),
				animalType,
				Integer.valueOf(newPatientDTO.getAge()), // value validated by @Valid @RequestBody
				newPatientDTO.getOwnerName(), newPatientDTO.getOwnerEmail());
		
		// checks if patient will not be duplicated
		ExampleMatcher caseInsensitiveMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
		Example<Patient> patientExample = Example.of(patient, caseInsensitiveMatcher);
		Optional<Patient> old = patientRepo.findOne(patientExample);

		if(old.isPresent()) {
			throw new PatientAlreadyExistsException(newPatientDTO);
		};
		
		Patient result = patientRepo.saveAndFlush(patient);
		
		return mapper.toDto(result);
	}

	private AnimalType findAnimalType(String animalTypeName) throws NotFoundException {
		Optional<AnimalType> result = animalTypeRepo.findOneByName(animalTypeName);
		return result.orElseThrow(() -> new NotFoundException("Animal type with name '" + animalTypeName + "' has not been found."));
	}

}