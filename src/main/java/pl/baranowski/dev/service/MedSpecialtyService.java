package pl.baranowski.dev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

@Service
public class MedSpecialtyService {
	
	@Autowired
	MedSpecialtyRepository medSpecialtyRepository;

	public MedSpecialtyService(MedSpecialtyRepository medSpecialtyRepository) {
		this.medSpecialtyRepository = medSpecialtyRepository;
	}

	public List<MedSpecialty> findAll() {
		return medSpecialtyRepository.findAll();
	}

	public MedSpecialty findByName(String specialty) {
		return medSpecialtyRepository.findByName(specialty).get(0);
	}

	public MedSpecialty put(MedSpecialty medSpecialty) {
		return medSpecialtyRepository.saveAndFlush(medSpecialty);
	}
	
	
}
