package pl.baranowski.dev.service;

import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.exception.NIPExistsException;
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

	public VetDTO addNew(VetDTO vetDTO) throws NIPExistsException {
		if(!vetRepository.findByNip(vetDTO.getNip()).isEmpty()) {
			throw new NIPExistsException();
		}
		Vet vet = mapToEntity.apply(vetDTO);
		VetDTO result = mapToDTO.apply(vetRepository.saveAndFlush(vet));
		return result;
	}

	public VetDTO getById(long id) {
		Vet vet = vetRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		return mapToDTO.apply(vet);
	}
	

	public Page<VetDTO> findAll(Pageable pageable) {
		Page<Vet> vets = vetRepository.findAll(pageable);
		Page<VetDTO> vetsDTO = new PageImpl<VetDTO>(
				vets.toList().stream()
					.map(mapToDTO)
					.collect(Collectors.toList()), 
				vets.getPageable(), 
				vets.getSize());
		return vetsDTO;
	}

	public boolean fire(Long decode) {
		return false;
	}
	
	private Function<VetDTO, Vet> mapToEntity = dto -> modelMapper.map(dto, Vet.class);
	private Function<Vet, VetDTO> mapToDTO = entity -> modelMapper.map(entity, VetDTO.class);

}
