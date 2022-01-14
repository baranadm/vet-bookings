package pl.baranowski.dev.service;

import java.util.function.Function;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.repository.VetRepository;

@Service
public class VetService {
	
	@Autowired
	ModelMapper modelMapper;

	@Autowired
	private final VetRepository vetRepository;

	public VetService(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	public Vet put(Vet vet) {
		return vetRepository.saveAndFlush(vet);
	}

	public VetDTO getById(long id) {
		Vet vet = vetRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		return entityToDTO.apply(vet);
	}
	
	private Function<Vet, VetDTO> entityToDTO = vet -> modelMapper.map(vet, VetDTO.class);
}
