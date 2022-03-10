package pl.baranowski.dev.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.medSpecialty.MedSpecialtyAlreadyExistsException;
import pl.baranowski.dev.mapper.MedSpecialtyMapper;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

@Service
public class MedSpecialtyService {
	private final MedSpecialtyRepository medSpecialtyRepository;
	private final MedSpecialtyMapper mapper;

	public MedSpecialtyService(MedSpecialtyRepository medSpecialtyRepository,
							   MedSpecialtyMapper mapper) {
		this.medSpecialtyRepository = medSpecialtyRepository;
		this.mapper = mapper;
	}

	public MedSpecialtyDTO getById(Long id) throws NotFoundException {
		MedSpecialty medSpecialty = medSpecialtyRepository.findById(id).orElseThrow(() -> new NotFoundException("Medical specialty with id="+id+" has not been found."));
		return mapper.toDto(medSpecialty);
	}
	
	public MedSpecialtyDTO findByName(String specialtyName) throws NotFoundException {
		MedSpecialty result = medSpecialtyRepository.findOneByName(specialtyName).orElseThrow(() -> new NotFoundException("Medical specialty with name="+specialtyName+" has not been found."));
		return mapper.toDto(result);
	}
	
	public List<MedSpecialtyDTO> findAll() {
		return medSpecialtyRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
	}

	public MedSpecialtyDTO addNew(String specialtyName) throws MedSpecialtyAlreadyExistsException {
		try {
			MedSpecialty result = medSpecialtyRepository
					.save(new MedSpecialty(specialtyName));
			return mapper.toDto(result);
		} catch(DataIntegrityViolationException e) {
			throw new MedSpecialtyAlreadyExistsException(specialtyName);
		}
	}
	
}
