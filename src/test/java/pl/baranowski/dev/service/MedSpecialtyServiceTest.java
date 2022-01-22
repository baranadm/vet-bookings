package pl.baranowski.dev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

@SpringBootTest
class MedSpecialtyServiceTest {

	@MockBean
	MedSpecialtyRepository medSpecialtyRepository;
	
	@Autowired
	MedSpecialtyService medSpecialtyService;
	
	@Autowired
	ModelMapper modelMapper;
	
	MedSpecialty cardio = new MedSpecialty(1L, "Kardiolog");
	MedSpecialty uro = new MedSpecialty(2l, "Urolog");
	List<MedSpecialty> specialties = Arrays.asList(cardio, uro);
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void getById_whenEntityExists_returnsDTO() {
		given(medSpecialtyRepository.findById(cardio.getId())).willReturn(Optional.ofNullable(cardio));
		assertEquals(mapToDto.apply(cardio), medSpecialtyService.getById(cardio.getId()));
	}

	@Test
	void getById_whenEntityDoesNot_throwsEntityNotFoundException() {
		given(medSpecialtyRepository.findById(3L)).willReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> medSpecialtyService.getById(3L));
	}
	
	@Test
	void findByName_whenEntitiesExist_returnsListOfDTOs() {
		given(medSpecialtyRepository.findByName(uro.getName())).willReturn(Collections.singletonList(uro));
		assertEquals(
				Collections.singletonList(uro).stream().map(mapToDto).collect(Collectors.toList()),
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
		assertEquals(specialties.stream().map(mapToDto).collect(Collectors.toList()),
				medSpecialtyService.findAll()
				);
	}
	
	@Test
	void findAll_whenEntitiesDoNotExists_returnsEmptyList() {
		given(medSpecialtyRepository.findAll()).willReturn(Collections.emptyList());
		assertEquals(Collections.emptyList(), medSpecialtyService.findAll());
	}
	
	@Test
	void addNew_whenNoDuplicate_returnsNewDTO() {
		
	}
	
	@Test
	void addNew_whenDuplicate_throwsMedSpecialtyAllreadyExistsException() {
		
	}

	private Function<MedSpecialty, MedSpecialtyDTO> mapToDto = entity -> modelMapper.map(entity, MedSpecialtyDTO.class);
}
