package pl.baranowski.dev.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.medSpecialty.MedSpecialtyAlreadyExistsException;
import pl.baranowski.dev.mapper.MedSpecialtyMapper;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

@Service
public class MedSpecialtyService {
	
	@Autowired
	MedSpecialtyMapper mapper;
	
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

	public MedSpecialtyDTO addNew(MedSpecialtyDTO medSpecialtyDTO) throws MedSpecialtyAlreadyExistsException {
		if(medSpecialtyRepository.findOneByName(medSpecialtyDTO.getName()).isEmpty()) {
			throw new MedSpecialtyAlreadyExistsException(medSpecialtyDTO.getName());
		}
		MedSpecialty medSpecialty = mapper.toEntity(medSpecialtyDTO);
		MedSpecialty result = medSpecialtyRepository
				.saveAndFlush(medSpecialty);
		MedSpecialtyDTO resultDTO = mapper.toDto(result);
		return resultDTO;
	}
	
}
