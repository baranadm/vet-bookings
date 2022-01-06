package pl.baranowski.dev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.PatientRepository;

@Service
public class PatientService {
	
	@Autowired
	PatientRepository patientRepo;
	@Autowired
	AnimalTypeRepository animalTypeRepo;
	
	public List<Patient> findAll() {
		return patientRepo.findAll();
	}
	
	public Patient put(String name, String animalTypeName, Integer age, String ownerName, String ownerEmail) {
		Patient patient = new Patient(
				name, 
				animalTypeRepo.findByName(animalTypeName).get(0), 
				age, 
				ownerName, 
				ownerEmail);
		return patientRepo.saveAndFlush(patient);
	}
	
}