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

	public MedSpecialty findById(Long id) {
		return medSpecialtyRepository.getById(id);
	}
	
	public List<MedSpecialty> findAll() {
		return medSpecialtyRepository.findAll();
	}

	public List<MedSpecialty> findByName(String specialty) {
		return medSpecialtyRepository.findByName(specialty);
	}

	public MedSpecialty addNew(MedSpecialty medSpecialty) {
		return medSpecialtyRepository.saveAndFlush(medSpecialty);
	}
	
	
}
