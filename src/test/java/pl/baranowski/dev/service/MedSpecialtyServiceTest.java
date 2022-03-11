package pl.baranowski.dev.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.medSpecialty.MedSpecialtyAlreadyExistsException;
import pl.baranowski.dev.mapper.MedSpecialtyMapper;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MedSpecialtyServiceTest {
    @Autowired
    MedSpecialtyRepository medSpecialtyRepository;
    @Autowired
    MedSpecialtyService medSpecialtyService;
    @Autowired
    MedSpecialtyMapper mapper;
    private MedSpecialty cardio;
    private MedSpecialty uro;
    private List<MedSpecialty> specialties;

    @BeforeEach
    void setUp() {
        cardio = medSpecialtyRepository.save(new MedSpecialty("Kardiolog"));
        uro = medSpecialtyRepository.save(new MedSpecialty("Urolog"));
        specialties = medSpecialtyRepository.findAll();
    }

    @AfterEach
    void tearDown() {
        medSpecialtyRepository.deleteAll();
    }

    @Test
    void getById_whenEntityExists_returnsDTO() throws NotFoundException {
        MedSpecialtyDTO actualDTO = medSpecialtyService.getById(cardio.getId());
        MedSpecialtyDTO expectedDTO = mapper.toDto(cardio);
        assertEquals(expectedDTO, actualDTO);
    }

    @Test
    void getById_whenEntityDoesNot_throwsEntityNotFoundException() {
        assertThrows(NotFoundException.class, () -> medSpecialtyService.getById(123L));
    }

    @Test
    void findByName_whenEntityExists_returnsDTO() throws NotFoundException {
        assertEquals(
                mapper.toDto(uro),
                medSpecialtyService.findByName(uro.getName())
        );
    }

    @Test
    void findByName_whenEntitiesDoNotExist_returnsEmptyList() {
        assertThrows(NotFoundException.class, () -> medSpecialtyService.findByName("ĄĘ"));
    }

    @Test
    void findAll_whenEntitiesExists_returnsListOfDTOs() {
        assertEquals(specialties.stream().map(mapper::toDto).collect(Collectors.toList()),
                     medSpecialtyService.findAll()
        );
    }

    @Test
    void findAll_whenEntitiesDoNotExists_returnsEmptyList() {
        medSpecialtyRepository.deleteAll();
        assertEquals(Collections.emptyList(), medSpecialtyService.findAll());
    }

    @Test
    void addNew_whenNoDuplicate_returnsNewDTO() throws MedSpecialtyAlreadyExistsException {
        String specialtyName = "Kąrdiolog";
        MedSpecialtyDTO resultDTO = medSpecialtyService.addNew(specialtyName);
        assertNotNull(resultDTO.getId());
        assertEquals(specialtyName, resultDTO.getName());
    }

    @Test
    void addNew_whenDuplicate_throwsMedSpecialtyAlreadyExistsException() {
        assertThrows(MedSpecialtyAlreadyExistsException.class,
                     () -> medSpecialtyService.addNew(cardio.getName()));
    }

}
