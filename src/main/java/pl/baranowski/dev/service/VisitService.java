package pl.baranowski.dev.service;

import java.util.List;
import java.util.function.Function;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
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

	public VisitDTO addNew(Long vetId, Long patientId, Long epoch) throws NewVisitNotPossibleException {
		// throws, if Vet is busy at epoch
		throwIfBusyAt(epoch, vetId);
		
		Visit result = visitRepository.saveAndFlush(
				new Visit(
						vetRepository.findById(vetId).orElseThrow(() -> new EntityNotFoundException("Doctor with id " + vetId + " has not been found.")), 
						patientRepository.findById(patientId).orElseThrow(() -> new EntityNotFoundException("Patient with id " + vetId + " has not been found.")), 
						epoch
						));
		return mapToDto.apply(result);
	}
	
	/*
	 * checks, if Vet has any visits at epoch
	 * TODO checking, if Vet is not on vacation
	 */
	private void throwIfBusyAt(long time, long vetId) throws NewVisitNotPossibleException {
		List<Visit> result = visitRepository.findByEpochAndVetId(time, vetId);
		if(result.size() >= 1) {
			String errorMessage = generateMessageWithArrayOfVisits("Vet", time, vetId, result);
			throw new NewVisitNotPossibleException(errorMessage);
		}
	}

	private String generateMessageWithArrayOfVisits(String field, long time, long id, List<Visit> result) {
		StringBuilder message = new StringBuilder(field + " with id " + id + "is busy at time " + time + ". ");
		message.append("Enrolled visits: [");
		result.forEach(visit -> message.append(visit.getId().toString() + ", "));
		// replaces last ", " to "]"
		message.replace(message.length()-2, message.length() -1, "]");
		return message.toString();
	}
	
	private Function<Visit, VisitDTO> mapToDto = entity -> modelMapper.map(entity, VisitDTO.class);
}
