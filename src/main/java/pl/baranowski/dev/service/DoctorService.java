package pl.baranowski.dev.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.exception.DoubledSpecialtyException;
import pl.baranowski.dev.exception.NIPExistsException;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.mapper.CustomMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.MedSpecialtyRepository;
import pl.baranowski.dev.repository.VetRepository;

@Service
public class DoctorService {
	

	@Autowired
	private final VetRepository vetRepository;
	
	@Autowired
	private final AnimalTypeRepository animalTypeRepository;
	
	@Autowired
	MedSpecialtyRepository medSpecialtyRepository;

	@Autowired
	CustomMapper mapper;
	
	public DoctorService(VetRepository vetRepository, AnimalTypeRepository animalTypeRepository) {
		this.vetRepository = vetRepository;
		this.animalTypeRepository = animalTypeRepository;
		
	}

	public DoctorDTO getById(long validatedId) throws EntityNotFoundException {
		Doctor vet = vetRepository.findById(validatedId).orElseThrow(EntityNotFoundException::new);
		return mapper.toDto(vet);
	}
	
	// TODO tests for below method
	public List<Doctor> findByAnimalTypeNameAndMedSpecialtyName(String animalTypeName, String medSpecialtyName) {
		// getting animalType
		List<AnimalType> ats = animalTypeRepository.findByName(animalTypeName);
		if(ats.size() < 1) {
			throw new EntityNotFoundException("Searching error: animalType with name [" + animalTypeName + "] has not been found.");
		}
		AnimalType at = ats.get(0);
		
		// getting medSpecialty
		List<MedSpecialty> mss = medSpecialtyRepository.findByName(medSpecialtyName);
		if(mss.size() <1) {
			throw new EntityNotFoundException("Searching error: medSpecialty with name [" + medSpecialtyName + "] has not been found.");
		}
		MedSpecialty ms = mss.get(0);
		
		List<Doctor> result = vetRepository.findByAnimalTypesAndMedSpecialties(at, ms);
		return result;
	}

	public Page<DoctorDTO> findAll(Pageable validatedPageable) {
		Page<Doctor> vets = vetRepository.findAll(validatedPageable);
		Page<DoctorDTO> vetsDTO = new PageImpl<DoctorDTO>(
				vets.toList().stream()
				.map(mapper::toDto)
				.collect(Collectors.toList()), 
				vets.getPageable(), 
				vets.getSize());
		return vetsDTO;
		
	}

	public DoctorDTO addNew(DoctorDTO validatedVetDTO) throws NIPExistsException {
		if(!vetRepository.findByNip(validatedVetDTO.getNip()).isEmpty()) {
			throw new NIPExistsException(); // NIP duplicated
		}
		Doctor vet = mapper.toEntity(validatedVetDTO);
		Doctor result = vetRepository.saveAndFlush(vet);
		DoctorDTO resultDTO = mapper.toDto(result);
		return resultDTO;
	}

	public void fire(Long id) throws DoctorNotActiveException {
		Optional<Doctor> vetOpt = vetRepository.findById(id);
		if(vetOpt.isPresent()) {
			Doctor vet = vetOpt.get();
			if(vet.getActive()) { // if Vet is active, sets active to false
				vet.setActive(false);
			} else { // if Vet is inactive, throws exception
				throw new DoctorNotActiveException().withCustomMessage("vet id: " + vet.getId() + " is not active");
			}
		} else {
			throw new EntityNotFoundException("Vet has not ben found");
		}
	}

	// should throw EntityNotFoundException if no vet
	// should throw EntityNotFoundException if no animalType
	// should throw DoubledSpecialtyException if vet already has animalType
	// should throw VetIsNotActiveException if vet is not active
	
	public DoctorDTO addAnimalType(Long vetId, Long animalTypeId) throws DoctorNotActiveException, DoubledSpecialtyException {
		
		// if vet not found, throw
		Doctor vet = vetRepository.findById(vetId).orElseThrow(() -> new EntityNotFoundException("vet with id " + vetId + " has not been found"));

		// if vet is not active, throw
		if(!vet.getActive()) {
			throw new DoctorNotActiveException().withCustomMessage("vet id: " + vet.getId() + " not found");
		}
		
		// if animal type not found, throw
		AnimalType animalType = animalTypeRepository.findById(animalTypeId).orElseThrow(() -> new EntityNotFoundException("animal type with id: " + animalTypeId + " has not been found"));
		
		// if vet has already that animal type specialty, throw
		if(vet.getAnimalTypes().contains(animalType)) {
			throw new DoubledSpecialtyException("animalType", animalType.getName());
		}
		
		// if everything is ok, update
		vet.addAnimalType(animalType);
		DoctorDTO result = mapper.toDto(vetRepository.saveAndFlush(vet));
		return result;
	}

	// should throw EntityNotFoundException if no vet
	// should throw EntityNotFoundException if no medSpecialty
	// should throw DoubledSpecialtyException if vet already has medSpecialty
	// should throw VetIsNotActiveException if vet is not active

	public DoctorDTO addMedSpecialty(Long vetId, Long msId) throws DoubledSpecialtyException, DoctorNotActiveException {
		// if no vet found, throw
		Doctor vet = vetRepository.findById(vetId)
				.orElseThrow(() -> new EntityNotFoundException("Doctor with id " + vetId + " has not been found."));
		// if vet is not active, throw
		if(!vet.getActive()) {
			throw new DoctorNotActiveException();
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
		DoctorDTO result = mapper.toDto(vetRepository.saveAndFlush(vet));
		return result;
	}

}
