package pl.baranowski.dev.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.doctor.DoctorAlreadyExistsException;
import pl.baranowski.dev.exception.doctor.DoctorDoubledSpecialtyException;
import pl.baranowski.dev.exception.doctor.DoctorNotActiveException;
import pl.baranowski.dev.mapper.DoctorMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.DoctorRepository;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorService.class);
    private final DoctorRepository doctorRepository;
    private final AnimalTypeRepository animalTypeRepository;
    private final MedSpecialtyRepository medSpecialtyRepository;
    private final DoctorMapper doctorMapper;

    public DoctorService(DoctorRepository doctorRepository,
                         AnimalTypeRepository animalTypeRepository,
                         MedSpecialtyRepository medSpecialtyRepository,
                         DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.animalTypeRepository = animalTypeRepository;
        this.medSpecialtyRepository = medSpecialtyRepository;
        this.doctorMapper = doctorMapper;
    }

    public DoctorDTO getDTO(long doctorId) throws NotFoundException {
        LOGGER.info("Received get() request with params: doctorId='{}'", doctorId);
        Doctor doctor = getEntity(doctorId);
        DoctorDTO result = doctorMapper.toDto(doctor);
        LOGGER.info("Doctor has been found and mapped: {}", result);
        return result;
    }

    public Doctor getEntity(long doctorId) throws NotFoundException {
        LOGGER.info("Received getEntity() request with params: doctorId='{}'", doctorId);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor with id=" + doctorId + " has not been found."));
        LOGGER.info("Doctor has been found! {}", doctor);
        return doctor;
    }

    // TODO tests for below method
    public List<Doctor> findByAnimalTypeNameAndMedSpecialtyName(String animalTypeName,
                                                                String medSpecialtyName) throws NotFoundException {
        AnimalType animalType = findAnimalType(animalTypeName);
        MedSpecialty medSpecialty = findMedSpecialty(medSpecialtyName);

        List<Doctor> result = doctorRepository.findByAnimalTypesAndMedSpecialties(animalType, medSpecialty);
        return result;
    }

    private AnimalType findAnimalType(String animalTypeName) throws NotFoundException {
        Optional<AnimalType> result = animalTypeRepository.findOneByName(animalTypeName);
        return result.orElseThrow(() -> new NotFoundException("Animal type with name '" + animalTypeName + "'has not been found."));
    }

    private MedSpecialty findMedSpecialty(String medSpecialtyName) throws NotFoundException {
        Optional<MedSpecialty> result = medSpecialtyRepository.findOneByName(medSpecialtyName);
        return result.orElseThrow(() -> new NotFoundException("Med specialty with name '" + medSpecialtyName + "' has not been found."));
    }

    public Page<DoctorDTO> findAll(Pageable validatedPageable) {
        Page<Doctor> doctors = doctorRepository.findAll(validatedPageable);
        Page<DoctorDTO> doctorsDTO = new PageImpl<>(
                doctors.toList().stream()
                        .map(doctorMapper::toDto)
                        .collect(Collectors.toList()),
                doctors.getPageable(),
                doctors.getSize());
        return doctorsDTO;

    }

    public DoctorDTO addNew(DoctorDTO doctorDTO) throws DoctorAlreadyExistsException {
        if (!doctorRepository.findByNip(doctorDTO.getNip()).isEmpty()) {
            throw new DoctorAlreadyExistsException(doctorDTO.getNip());
        }
        Doctor doctor = doctorMapper.toEntity(doctorDTO);
        Doctor result = doctorRepository.saveAndFlush(doctor);
        DoctorDTO resultDTO = doctorMapper.toDto(result);
        return resultDTO;
    }

    public DoctorDTO fire(Long id) throws DoctorNotActiveException, NotFoundException {
        LOGGER.info("/fire id={}", id);

        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        LOGGER.debug("repo returned: {}", doctorOpt);

        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            if (doctor.getActive()) { // if Doctor is active, sets active to false
                doctor.setActive(false);
                LOGGER.debug("Doctor set to inactive: {}", doctor);
                DoctorDTO firedDoctor = doctorMapper.toDto(doctorRepository.save(doctor));
                return firedDoctor;
            } else { // if Doctor is inactive, throws exception
                throw new DoctorNotActiveException(doctor.getId());
            }
        } else {
            throw new NotFoundException("Doctor has not ben found");
        }
    }

    // should throw EntityNotFoundException if no Doctor
    // should throw EntityNotFoundException if no animalType
    // should throw DoubledSpecialtyException if Doctor already has animalType
    // should throw DoctorIsNotActiveException if Doctor is not active

    public DoctorDTO addAnimalType(Long doctorId,
                                   Long animalTypeId) throws NotFoundException, DoctorNotActiveException, DoctorDoubledSpecialtyException {

        // if Doctor not found, throw
        Doctor doctor = getEntity(doctorId);

        // if Doctor is not active, throw
        if (!doctor.getActive()) {
            throw new DoctorNotActiveException(doctor.getId());
        }

        // if animal type not found, throw
        AnimalType animalType = animalTypeRepository.findById(animalTypeId)
                .orElseThrow(() -> new NotFoundException("animal type with id: " + animalTypeId + " has not been found"));

        // if Doctor has already that animal type specialty, throw
        if (doctor.getAnimalTypes().contains(animalType)) {
            throw new DoctorDoubledSpecialtyException("Animal Type");
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

    public DoctorDTO addMedSpecialty(Long doctorId,
                                     Long msId) throws NotFoundException, DoctorNotActiveException, DoctorDoubledSpecialtyException {
        // if no Doctor found, throw
        Doctor doctor = getEntity(doctorId);
        // if Doctor is not active, throw
        if (!doctor.getActive()) {
            throw new DoctorNotActiveException(doctor.getId());
        }
        // if no medSpecialty found, throw
        MedSpecialty medSpecialty = medSpecialtyRepository.findById(msId)
                .orElseThrow(() -> new NotFoundException("Medical specialty with id " + msId + " has not been found."));

        // if Doctor already has this med specialty
        // since there can't be two medSpecialties with same name, we can check it with .equals()
        if (doctor.getMedSpecialties().contains(medSpecialty)) {
            throw new DoctorDoubledSpecialtyException("Medical Specialty");
        }

        // if everything is ok, then add medSpecialty to Doctor
        doctor.addMedSpecialty(medSpecialty);

        // save (update) to DB
        DoctorDTO result = doctorMapper.toDto(doctorRepository.saveAndFlush(doctor));
        return result;
    }

}
