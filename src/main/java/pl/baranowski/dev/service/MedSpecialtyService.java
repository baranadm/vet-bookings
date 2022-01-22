package pl.baranowski.dev.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.MedSpecialtyAllreadyExistsException;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

@Service
public class MedSpecialtyService {
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	MedSpecialtyRepository medSpecialtyRepository;

	public MedSpecialtyService(MedSpecialtyRepository medSpecialtyRepository) {
		this.medSpecialtyRepository = medSpecialtyRepository;
	}

	public MedSpecialtyDTO getById(Long id) {
		Optional<MedSpecialty> result = medSpecialtyRepository.findById(id);
		MedSpecialty medSpecialty = result.orElseThrow(() -> new EntityNotFoundException("med specialty not found"));
		return mapToDTO.apply(medSpecialty);
	}
	
	public List<MedSpecialtyDTO> findByName(String specialty) {
		return medSpecialtyRepository.findByName(specialty).stream().map(mapToDTO).collect(Collectors.toList());
	}
	
	public List<MedSpecialtyDTO> findAll() {
		return medSpecialtyRepository.findAll().stream().map(mapToDTO).collect(Collectors.toList());
	}

	public MedSpecialtyDTO addNew(MedSpecialtyDTO medSpecialtyDTO) throws MedSpecialtyAllreadyExistsException, EmptyFieldException {
		if(!this.findByName(medSpecialtyDTO.getName()).isEmpty()) {
			throw new MedSpecialtyAllreadyExistsException();
		}
		MedSpecialty resultEntity = medSpecialtyRepository
				.saveAndFlush(mapToEntity.apply(medSpecialtyDTO));
		return mapToDTO.apply(resultEntity);
	}
	
	private Function<MedSpecialty, MedSpecialtyDTO> mapToDTO = entity -> modelMapper.map(entity, MedSpecialtyDTO.class);
	private Function<MedSpecialtyDTO, MedSpecialty> mapToEntity = dto -> modelMapper.map(dto, MedSpecialty.class);
	
	
}
