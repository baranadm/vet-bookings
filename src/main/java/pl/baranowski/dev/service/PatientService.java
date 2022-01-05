package pl.baranowski.dev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.repository.PatientRepository;

@Service
public class PatientService {
	
	@Autowired
	private final PatientRepository patientRepo;

	public PatientService(PatientRepository patientRepo) {
		this.patientRepo = patientRepo;
	}
	
	public List<Patient> findAll() {
		return patientRepo.findAll();
	}
	
	public Patient put(Patient patient) {
		return patientRepo.saveAndFlush(patient);
	}
	
}