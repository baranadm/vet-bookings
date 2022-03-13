package pl.baranowski.dev.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.medSpecialty.MedSpecialtyAlreadyExistsException;
import pl.baranowski.dev.mapper.MedSpecialtyMapper;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedSpecialtyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MedSpecialtyService.class);
    private final MedSpecialtyRepository medSpecialtyRepository;
    private final MedSpecialtyMapper mapper;

    public MedSpecialtyService(MedSpecialtyRepository medSpecialtyRepository,
                               MedSpecialtyMapper mapper) {
        this.medSpecialtyRepository = medSpecialtyRepository;
        this.mapper = mapper;
    }

    public MedSpecialtyDTO getById(Long id) throws NotFoundException {
        LOGGER.debug("getById(id='{}')", id);

        MedSpecialty result = medSpecialtyRepository.findById(id).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Medical specialty with id=" + id + " has not been found.");
            LOGGER.error(e.getMessage(), e);
            return e;
        });
        LOGGER.debug("MedSpecialty found: {}", result);

        MedSpecialtyDTO resultDTO = mapper.toDto(result);
        LOGGER.debug("Returning mapped result: {}", resultDTO);
        return resultDTO;
    }

    public MedSpecialtyDTO findByName(String specialtyName) throws NotFoundException {
        LOGGER.debug("findByName(name='{}')", specialtyName);
        MedSpecialty result = medSpecialtyRepository.findOneByName(specialtyName).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Medical specialty with name=" + specialtyName + " has not been found.");
            LOGGER.error(e.getMessage(), e);
            return e;
        });
        LOGGER.debug("MedSpecialty found: {}", result);

        MedSpecialtyDTO resultDTO = mapper.toDto(result);
        LOGGER.debug("Returning mapped result: {}", resultDTO);
        return resultDTO;
    }

    public List<MedSpecialtyDTO> findAll() {
        LOGGER.debug("findAll()");
        List<MedSpecialtyDTO> result = medSpecialtyRepository.findAll()
                                                             .stream()
                                                             .map(mapper::toDto)
                                                             .collect(Collectors.toList());
        LOGGER.debug("MedSpecialties found: {}, returning result.", result.size());
        return result;
    }

    public MedSpecialtyDTO addNew(String specialtyName) throws MedSpecialtyAlreadyExistsException {
        LOGGER.debug("addNew(name='{}')", specialtyName);
        try {
            MedSpecialty result = medSpecialtyRepository
                    .save(new MedSpecialty(specialtyName));
            LOGGER.debug("New MedSpecialty saved, result: {}", result);
            MedSpecialtyDTO resultDTO = mapper.toDto(result);
            LOGGER.debug("Mapping and returning DTO: {}", resultDTO);
            return resultDTO;
        } catch (DataIntegrityViolationException e) {
            LOGGER.error(e.getMessage(), e);
            MedSpecialtyAlreadyExistsException medSpecialtyException = new MedSpecialtyAlreadyExistsException(
                    specialtyName);
            LOGGER.error("Throwing ApiException: {}", medSpecialtyException.getMessage(), medSpecialtyException);
            throw medSpecialtyException;
        }
    }

}
