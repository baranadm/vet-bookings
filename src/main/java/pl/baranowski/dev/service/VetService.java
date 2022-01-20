package pl.baranowski.dev.service;

import java.util.Optional;
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
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.exception.DoubledSpecialtyException;
import pl.baranowski.dev.exception.NIPExistsException;
import pl.baranowski.dev.exception.VetNotActiveException;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.VetRepository;

@Service
public class VetService {
	
	@Autowired
	ModelMapper modelMapper;

	@Autowired
	private final VetRepository vetRepository;
	
	@Autowired
	private final AnimalTypeRepository animalTypeRepository;

	public VetService(VetRepository vetRepository, AnimalTypeRepository animalTypeRepository) {
		this.vetRepository = vetRepository;
		this.animalTypeRepository = animalTypeRepository;
	}

	public VetDTO getById(long validatedId) throws EntityNotFoundException {
		Vet vet = vetRepository.findById(validatedId).orElseThrow(EntityNotFoundException::new);
		return mapToDTO.apply(vet);
	}

	public Page<VetDTO> findAll(Pageable validatedPageable) {
		Page<Vet> vets = vetRepository.findAll(validatedPageable);
		Page<VetDTO> vetsDTO = new PageImpl<VetDTO>(
				vets.toList().stream()
				.map(mapToDTO)
				.collect(Collectors.toList()), 
				vets.getPageable(), 
				vets.getSize());
		return vetsDTO;
		
	}

	public VetDTO addNew(VetDTO validatedVetDTO) throws NIPExistsException {
		if(!vetRepository.findByNip(validatedVetDTO.getNip()).isEmpty()) {
			throw new NIPExistsException();
		}
		Vet vet = mapToEntity.apply(validatedVetDTO);
		Vet result = vetRepository.saveAndFlush(vet);
		VetDTO resultDTO = mapToDTO.apply(result);
		return resultDTO;
	}

	public boolean fire(Long id) throws VetNotActiveException {
		Optional<Vet> vetOpt = vetRepository.findById(id);
		if(vetOpt.isPresent()) {
			Vet vet = vetOpt.get();
			if(vet.getActive()) {
				vet.setActive(false);
				return true;
			} else {
				throw new VetNotActiveException().withCustomMessage("vet id: " + vet.getId() + " not found");
			}
		}
		return false;
	}

	// should throw EntityNotFoundException if no vet
	// should throw EntityNotFoundException if no animalType
	// should throw DoubledSpecialtyException if vet already has animalType
	// should throw VetIsNotActiveException if vet is not active
	
	public VetDTO addAnimalType(Long vetId, Long animalTypeId) throws VetNotActiveException, DoubledSpecialtyException {
		
		// if vet not found, throw
		Vet vet = vetRepository.findById(vetId).orElseThrow(() -> new EntityNotFoundException("vet with id " + vetId + " has not been found"));

		// if vet is not active, throw
		if(!vet.getActive()) {
			throw new VetNotActiveException().withCustomMessage("vet id: " + vet.getId() + " not found");
		}
		
		// if animal type not found, throw
		AnimalType animalType = animalTypeRepository.findById(animalTypeId).orElseThrow(() -> new EntityNotFoundException("animal type with id: " + animalTypeId + " has not been found"));
		
		// if vet has animal type specialty, throw
		if(vet.getAnimalTypes().contains(animalType)) {
			throw new DoubledSpecialtyException("animalType", animalType.getName());
		}
		
		// if everything is ok, update and return vet
		vet.addAnimalType(animalType);
		Vet result = vetRepository.saveAndFlush(vet);
		
		return mapToDTO.apply(result);
	}

	Function<VetDTO, Vet> mapToEntity = dto -> modelMapper.map(dto, Vet.class);
	Function<Vet, VetDTO> mapToDTO = entity -> modelMapper.map(entity, VetDTO.class);

}
