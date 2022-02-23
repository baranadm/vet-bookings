package pl.baranowski.dev.service;

import java.util.List;
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
import pl.baranowski.dev.exception.PatientAllreadyExistsException;
import pl.baranowski.dev.mapper.CustomMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.PatientRepository;

@Service
public class PatientService {
	
	@Autowired
	PatientRepository patientRepo;
	@Autowired
	AnimalTypeRepository animalTypeRepo;
	@Autowired
	CustomMapper mapper;
	
	public Page<PatientDTO> findAll(Pageable pageable) {
		Page<Patient> result = patientRepo.findAll(pageable);
		return result.map(entity -> mapper.toDto(entity));
	}
	
	public PatientDTO getDto(Long patientId) {
		Patient result = get(patientId);
		return mapper.toDto(result);
	}

	protected Patient get(Long patientId) {
		Patient result = patientRepo.findById(patientId).orElseThrow(() -> new EntityNotFoundException("Patient with id " + patientId + " has not been found."));
		return result;
	}

	public PatientDTO addNew(NewPatientDTO newDTO) throws PatientAllreadyExistsException {
		// find provided animalType exists, or throw error
		List<AnimalType> animalTypes = animalTypeRepo.findByName(newDTO.getAnimalTypeName());

		// throw error if no animalType found
		if(animalTypes.size() < 1) {
			throw new EntityNotFoundException("animal type with name " + newDTO.getAnimalTypeName() + " has not been found");
		}
		// .. animalType has been found
		Patient patient = new Patient(newDTO.getName(), 
				animalTypes.get(0), // should contain only one record (dulicated names are rejected on creation)
				Integer.valueOf(newDTO.getAge()), // value validated by @Valid @RequestBody
				newDTO.getOwnerName(), newDTO.getOwnerEmail());
		
		// checks if patient will not be duplicated
		ExampleMatcher caseInsensitiveMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
		Example<Patient> patientExample = Example.of(patient, caseInsensitiveMatcher);
		Optional<Patient> old = patientRepo.findOne(patientExample);
		if(old.isPresent()) {
			throw new PatientAllreadyExistsException("Patient " + patient.getName() + "allready exists in database, and has id: " + old.get().getId());
		};
		
		Patient result = patientRepo.saveAndFlush(patient);
		
		return mapper.toDto(result);
	}
	
}