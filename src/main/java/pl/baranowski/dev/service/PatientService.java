package pl.baranowski.dev.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.NewPatientDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.exception.PatientAllreadyExistsException;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.PatientRepository;

@Service
public class PatientService {
	
	@Autowired
	PatientRepository patientRepo;
	@Autowired
	AnimalTypeRepository animalTypeRepo;
	@Autowired
	ModelMapper modelMapper;
	
	public List<PatientDTO> findAll() {
		List<Patient> result = patientRepo.findAll();
		return result.stream().map(entity -> modelMapper.map(entity, PatientDTO.class)).collect(Collectors.toList());
	}
	
//	public Patient addNew(String name, String animalTypeName, Integer age, String ownerName, String ownerEmail) {
//		Patient patient = new Patient(
//				name, 
//				animalTypeRepo.findByName(animalTypeName).get(0), 
//				age, 
//				ownerName, 
//				ownerEmail);
//		return patientRepo.saveAndFlush(patient);
//	}

	public PatientDTO getById(Long id) {
		return null;
	}

	public PatientDTO addNew(NewPatientDTO newDTO) throws PatientAllreadyExistsException {
		// find provided animalType exists, or throw error
		List<AnimalType> animalTypes = animalTypeRepo.findByName(newDTO.getAnimalTypeName());

		// throw error if no animalType found
		if(animalTypes.size() < 1) {
			throw new EntityNotFoundException("animal type with name " + newDTO.getAnimalTypeName() + " has not been found");
		}
		
		Patient patient = new Patient(newDTO.getName(), 
				animalTypes.get(0), // should contain only one record (dulicated names are rejected on creation)
				newDTO.getAge(), newDTO.getOwnerName(), newDTO.getOwnerEmail());
		
		// checks if patient would not be duplicated
		ExampleMatcher caseInsensitiveMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
		Example<Patient> patientExample = Example.of(patient, caseInsensitiveMatcher);
		Optional<Patient> old = patientRepo.findOne(patientExample);
		if(old.isPresent()) {
			throw new PatientAllreadyExistsException("Patient " + patient.getName() + "allready exists in database, and has id: " + old.get().getId());
		};
		
		Patient result = patientRepo.saveAndFlush(patient);
		
		return modelMapper.map(result, PatientDTO.class);
	}
	
}