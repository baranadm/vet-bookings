package pl.baranowski.dev.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
		return mapToDTO.apply(medSpecialtyRepository.getById(id));
	}
	
	public List<MedSpecialtyDTO> findAll() {
		return medSpecialtyRepository.findAll().stream().map(mapToDTO).collect(Collectors.toList());
	}

	public List<MedSpecialtyDTO> findByName(String specialty) throws EmptyFieldException {
		return medSpecialtyRepository.findByName(specialty).stream().map(mapToDTO).collect(Collectors.toList());
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
