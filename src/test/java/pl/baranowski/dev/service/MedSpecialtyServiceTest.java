package pl.baranowski.dev.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.medSpecialty.MedSpecialtyAlreadyExistsException;
import pl.baranowski.dev.mapper.MedSpecialtyMapper;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class MedSpecialtyServiceTest {

    @MockBean
    MedSpecialtyRepository medSpecialtyRepository;

    @Autowired
    MedSpecialtyService medSpecialtyService;

    @Autowired
    MedSpecialtyMapper mapper;

    MedSpecialty cardio = new MedSpecialty(1L, "Kardiolog");
    MedSpecialty uro = new MedSpecialty(2L, "Urolog");
    List<MedSpecialty> specialties = Arrays.asList(cardio, uro);

    @BeforeEach
    void setUp() {
    }

    @Test
    void getById_whenEntityExists_returnsDTO() throws NotFoundException {
        MedSpecialtyDTO expectedDTO = mapper.toDto(cardio);
        given(medSpecialtyRepository.findById(cardio.getId())).willReturn(Optional.ofNullable(cardio));

        MedSpecialtyDTO actualDTO = medSpecialtyService.getById(cardio.getId());
        assertEquals(expectedDTO, actualDTO);
    }

    @Test
    void getById_whenEntityDoesNot_throwsEntityNotFoundException() {
        given(medSpecialtyRepository.findById(3L)).willReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> medSpecialtyService.getById(3L));
    }

    @Test
    void findByName_whenEntitiesExist_returnsListOfDTOs() {
        given(medSpecialtyRepository.findByName(uro.getName())).willReturn(Collections.singletonList(uro));
        assertEquals(
                Stream.of(uro).map(mapper::toDto).collect(Collectors.toList()),
                medSpecialtyService.findByName(uro.getName())
        );
    }

    @Test
    void findByName_whenEntitiesDoNotExist_returnsEmptyList() {
        given(medSpecialtyRepository.findByName("Neurolog")).willReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), medSpecialtyService.findByName("Neurolog"));
    }

    @Test
    void findAll_whenEntitiesExists_returnsListOfDTOs() {
        given(medSpecialtyRepository.findAll()).willReturn(specialties);
        assertEquals(specialties.stream().map(mapper::toDto).collect(Collectors.toList()),
                     medSpecialtyService.findAll()
        );
    }

    @Test
    void findAll_whenEntitiesDoNotExists_returnsEmptyList() {
        given(medSpecialtyRepository.findAll()).willReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), medSpecialtyService.findAll());
    }

    @Test
    void addNew_whenNoDuplicate_returnsNewDTO() throws MedSpecialtyAlreadyExistsException {
        MedSpecialty newMedSpecialty = new MedSpecialty(1L, "Cardio");
        MedSpecialtyDTO expectedDTO = mapper.toDto(newMedSpecialty);

        given(medSpecialtyRepository.findOneByName(newMedSpecialty.getName())).willReturn(Optional.empty());
        given(medSpecialtyRepository.save(newMedSpecialty)).willReturn(newMedSpecialty);

        MedSpecialtyDTO resultDTO = medSpecialtyService.addNew(expectedDTO);
        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void addNew_whenDuplicate_throwsMedSpecialtyAlreadyExistsException() {
        MedSpecialty medSpecialty = new MedSpecialty(1L, "Cardio");
        given(medSpecialtyRepository.findOneByName(medSpecialty.getName())).willReturn(Optional.of(medSpecialty));

        assertThrows(MedSpecialtyAlreadyExistsException.class,
                     () -> medSpecialtyService.addNew(mapper.toDto(medSpecialty)));
    }

}
