package pl.baranowski.dev.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.MedSpecialtyAllreadyExistsException;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

@Service
public class MedSpecialtyService {
	
	@Autowired
	CustomMapper mapper;
	
	@Autowired
	MedSpecialtyRepository medSpecialtyRepository;

	public MedSpecialtyService(MedSpecialtyRepository medSpecialtyRepository) {
		this.medSpecialtyRepository = medSpecialtyRepository;
	}

	public MedSpecialtyDTO getById(Long id) {
		Optional<MedSpecialty> result = medSpecialtyRepository.findById(id);
		MedSpecialty medSpecialty = result.orElseThrow(() -> new EntityNotFoundException("med specialty not found"));
		return mapper.toDto(medSpecialty);
	}
	
	public List<MedSpecialtyDTO> findByName(String specialty) {
		return medSpecialtyRepository.findByName(specialty).stream().map(mapper::toDto).collect(Collectors.toList());
	}
	
	public List<MedSpecialtyDTO> findAll() {
		return medSpecialtyRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
	}

	public MedSpecialtyDTO addNew(MedSpecialtyDTO medSpecialtyDTO) throws MedSpecialtyAllreadyExistsException {
		MedSpecialty ms = mapper.toEntity(medSpecialtyDTO);
		if(!findByName(ms.getName()).isEmpty()) {
			throw new MedSpecialtyAllreadyExistsException();
		}
		MedSpecialty result = medSpecialtyRepository
				.saveAndFlush(ms);
		MedSpecialtyDTO resultDTO = mapper.toDto(result);
		return resultDTO;
	}
	
}
