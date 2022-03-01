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
import pl.baranowski.dev.mapper.DoctorMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.MedSpecialtyRepository;
import pl.baranowski.dev.repository.DoctorRepository;

@Service
public class DoctorService {
	private final DoctorRepository doctorRepository;
	private final AnimalTypeRepository animalTypeRepository;
	MedSpecialtyRepository medSpecialtyRepository;
	DoctorMapper doctorMapper;

	public DoctorService(DoctorRepository doctorRepository, AnimalTypeRepository animalTypeRepository, MedSpecialtyRepository medSpecialtyRepository, DoctorMapper doctorMapper) {
		this.doctorRepository = doctorRepository;
		this.animalTypeRepository = animalTypeRepository;
		this.medSpecialtyRepository = medSpecialtyRepository;
		this.doctorMapper = doctorMapper;
	}

	public DoctorDTO getDto(long doctorId) throws EntityNotFoundException {
		Doctor doctor = get(doctorId);
		return doctorMapper.toDto(doctor);
	}

	public Doctor get(long doctorId) {
		Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(EntityNotFoundException::new);
		return doctor;
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
		
		List<Doctor> result = doctorRepository.findByAnimalTypesAndMedSpecialties(at, ms);
		return result;
	}

	public Page<DoctorDTO> findAll(Pageable validatedPageable) {
		Page<Doctor> doctors = doctorRepository.findAll(validatedPageable);
		Page<DoctorDTO> doctorsDTO = new PageImpl<DoctorDTO>(
				doctors.toList().stream()
				.map(doctorMapper::toDto)
				.collect(Collectors.toList()), 
				doctors.getPageable(), 
				doctors.getSize());
		return doctorsDTO;
		
	}

	public DoctorDTO addNew(DoctorDTO validatedDoctorDTO) throws NIPExistsException {
		if(!doctorRepository.findByNip(validatedDoctorDTO.getNip()).isEmpty()) {
			throw new NIPExistsException(); // NIP duplicated
		}
		Doctor doctor = doctorMapper.toEntity(validatedDoctorDTO);
		Doctor result = doctorRepository.saveAndFlush(doctor);
		DoctorDTO resultDTO = doctorMapper.toDto(result);
		return resultDTO;
	}

	public void fire(Long id) throws DoctorNotActiveException {
		Optional<Doctor> doctorOpt = doctorRepository.findById(id);
		if(doctorOpt.isPresent()) {
			Doctor doctor = doctorOpt.get();
			if(doctor.getActive()) { // if Doctor is active, sets active to false
				doctor.setActive(false);
			} else { // if Doctor is inactive, throws exception
				throw new DoctorNotActiveException().withCustomMessage("Doctor id: " + doctor.getId() + " is not active");
			}
		} else {
			throw new EntityNotFoundException("Doctor has not ben found");
		}
	}

	// should throw EntityNotFoundException if no Doctor
	// should throw EntityNotFoundException if no animalType
	// should throw DoubledSpecialtyException if Doctor already has animalType
	// should throw DoctorIsNotActiveException if Doctor is not active
	
	public DoctorDTO addAnimalType(Long doctorId, Long animalTypeId) throws DoctorNotActiveException, DoubledSpecialtyException {
		
		// if Doctor not found, throw
		Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFoundException("Doctor with id " + doctorId + " has not been found"));

		// if Doctor is not active, throw
		if(!doctor.getActive()) {
			throw new DoctorNotActiveException().withCustomMessage("Doctor id: " + doctor.getId() + " not found");
		}
		
		// if animal type not found, throw
		AnimalType animalType = animalTypeRepository.findById(animalTypeId).orElseThrow(() -> new EntityNotFoundException("animal type with id: " + animalTypeId + " has not been found"));
		
		// if Doctor has already that animal type specialty, throw
		if(doctor.getAnimalTypes().contains(animalType)) {
			throw new DoubledSpecialtyException("animalType", animalType.getName());
		}
		
		// if everything is ok, update
		doctor.addAnimalType(animalType);
		DoctorDTO result = doctorMapper.toDto(doctorRepository.saveAndFlush(doctor));
		return result;
	}

	// should throw EntityNotFoundException if no Doctor
	// should throw EntityNotFoundException if no medSpecialty
	// should throw DoubledSpecialtyException if Doctor already has medSpecialty
	// should throw DoctorIsNotActiveException if Doctor is not active

	public DoctorDTO addMedSpecialty(Long doctorId, Long msId) throws DoubledSpecialtyException, DoctorNotActiveException {
		// if no Doctor found, throw
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new EntityNotFoundException("Doctor with id " + doctorId + " has not been found."));
		// if Doctor is not active, throw
		if(!doctor.getActive()) {
			throw new DoctorNotActiveException();
		}
		// if no medSpecialty found, throw
		MedSpecialty ms = medSpecialtyRepository.findById(msId)
				.orElseThrow(() -> new EntityNotFoundException("Medical specialty with id " + msId + " has not been found."));
		
		// if Doctor already has this med specialty
		// since there can't be two medSpecialties with same name, we can check it with .equals()
		if(doctor.getMedSpecialties().contains(ms)) {
			throw new DoubledSpecialtyException("medical specialty", ms.getName());
		}
		
		// if everything is ok, then add medSpecialty to Doctor
		doctor.addMedSpecialty(ms);
		
		// save (update) to DB
		DoctorDTO result = doctorMapper.toDto(doctorRepository.saveAndFlush(doctor));
		return result;
	}

}
