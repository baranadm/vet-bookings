package pl.baranowski.dev.service;

import java.util.function.Function;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.repository.PatientRepository;
import pl.baranowski.dev.repository.VetRepository;
import pl.baranowski.dev.repository.VisitRepository;

@Service
public class VisitService {

	@Autowired
	VisitRepository visitRepository;
	@Autowired
	PatientRepository patientRepository;
	@Autowired
	VetRepository vetRepository;
	@Autowired
	ModelMapper modelMapper;

	public VisitDTO getById(long id) {
		Visit result = visitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Visit with id: " + id+" has not been found"));
		return modelMapper.map(result, VisitDTO.class);
	}
	
	public Page<VisitDTO> findAll(Pageable pageable) {
		Page<Visit> result = visitRepository.findAll(pageable); 
		Page<VisitDTO> pageOfDTOs = result.map(mapToDto);
		return pageOfDTOs;
	}

	public VisitDTO addNew(Long vetId, Long patientId, Long epoch) {
		Visit result = visitRepository.saveAndFlush(
				new Visit(
						null, //id
						vetRepository.findById(vetId).get(), 
						patientRepository.findById(patientId).get(), 
						epoch,
						false //isConfirmed
						));
		return mapToDto.apply(result);
	}
	
	private Function<Visit, VisitDTO> mapToDto = entity -> modelMapper.map(entity, VisitDTO.class);
}
