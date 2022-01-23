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
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.exception.DoubledSpecialtyException;
import pl.baranowski.dev.exception.NIPExistsException;
import pl.baranowski.dev.exception.VetNotActiveException;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.MedSpecialtyRepository;
import pl.baranowski.dev.repository.VetRepository;

@Service
public class VetService {
	
	@Autowired
	ModelMapper modelMapper;

	@Autowired
	private final VetRepository vetRepository;
	
	@Autowired
	private final AnimalTypeRepository animalTypeRepository;
	
	@Autowired
	MedSpecialtyRepository medSpecialtyRepository;

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
			throw new NIPExistsException(); // NIP duplicated
		}
		Vet vet = mapToEntity.apply(validatedVetDTO);
		Vet result = vetRepository.saveAndFlush(vet);
		VetDTO resultDTO = mapToDTO.apply(result);
		return resultDTO;
	}

	public void fire(Long id) throws VetNotActiveException {
		Optional<Vet> vetOpt = vetRepository.findById(id);
		if(vetOpt.isPresent()) {
			Vet vet = vetOpt.get();
			if(vet.getActive()) { // if Vet is active, sets active to false
				vet.setActive(false);
			} else { // if Vet is inactive, throws exception
				throw new VetNotActiveException().withCustomMessage("vet id: " + vet.getId() + " is not active");
			}
		} else {
			throw new EntityNotFoundException("Vet has not ben found");
		}
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
		
		// if vet has already that animal type specialty, throw
		if(vet.getAnimalTypes().contains(animalType)) {
			throw new DoubledSpecialtyException("animalType", animalType.getName());
		}
		
		// if everything is ok, update
		vet.addAnimalType(animalType);
		VetDTO result = mapToDTO.apply(vetRepository.saveAndFlush(vet));
		return result;
	}

	// should throw EntityNotFoundException if no vet
	// should throw EntityNotFoundException if no medSpecialty
	// should throw DoubledSpecialtyException if vet already has medSpecialty
	// should throw VetIsNotActiveException if vet is not active

	public VetDTO addMedSpecialty(Long vetId, Long msId) throws DoubledSpecialtyException, VetNotActiveException {
		// if no vet found, throw
		Vet vet = vetRepository.findById(vetId)
				.orElseThrow(() -> new EntityNotFoundException("Doctor with id " + vetId + " has not been found."));
		// if vet is not active, throw
		if(!vet.getActive()) {
			throw new VetNotActiveException();
		}
		// if no medSpecialty found, throw
		MedSpecialty ms = medSpecialtyRepository.findById(msId)
				.orElseThrow(() -> new EntityNotFoundException("Medical specialty with id " + msId + " has not been found."));
		
		// if vet already has this med specialty
		// since there can't be two medSpecialties with same name, we can check it with .equals()
		if(vet.getMedSpecialties().contains(ms)) {
			throw new DoubledSpecialtyException("medical specialty", ms.getName());
		}
		
		// if everything is ok, then add medSpecialty to vet
		vet.addMedSpecialty(ms);
		
		// save (update) to DB
		VetDTO result = mapToDTO.apply(vetRepository.saveAndFlush(vet));
		return result;
	}
	
	Function<VetDTO, Vet> mapToEntity = dto -> modelMapper.map(dto, Vet.class);
	Function<Vet, VetDTO> mapToDTO = entity -> modelMapper.map(entity, VetDTO.class);

}
