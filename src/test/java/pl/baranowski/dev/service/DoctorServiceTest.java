package pl.baranowski.dev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pl.baranowski.dev.builder.DoctorBuilder;
import pl.baranowski.dev.builder.DoctorDTOBuilder;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.*;
import pl.baranowski.dev.exception.doctor.DoctorAlreadyExistsException;
import pl.baranowski.dev.exception.doctor.DoctorDoubledSpecialtyException;
import pl.baranowski.dev.exception.doctor.DoctorNotActiveException;
import pl.baranowski.dev.mapper.DoctorMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.DoctorRepository;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

// TODO jak zmienić nazwę klasy z Vet na Doctor??
@SpringBootTest
class DoctorServiceTest {

    @MockBean
    DoctorRepository doctorRepository;
    @MockBean
    AnimalTypeRepository animalTypeRepository;
    @MockBean
    MedSpecialtyRepository medSpecialtyRepository;

    @Autowired
    DoctorService doctorService;

    @Autowired
    DoctorMapper doctorMapper;

    private Doctor mostowiak;
    private List<DoctorDTO> doctorsList;

    @BeforeEach
    void setUp() {
        this.mostowiak = new DoctorBuilder().name("Mark").surname("Most-o-wiack").nip("1181328620").id(1L)
                .hourlyRate(new BigDecimal(150).setScale(2))
                .build();
        doctorsList = new ArrayList<>();

        doctorsList.add(new DoctorDTOBuilder().name("Robert")
                                .surname("Kubica")
                                .hourlyRate("100000.00")
                                .nip("1213141516")
                                .build());
        doctorsList.add(new DoctorDTOBuilder().name("Mirosław")
                                .surname("Rosomak")
                                .hourlyRate("100.00")
                                .nip("0987654321")
                                .build());
        doctorsList.add(new DoctorDTOBuilder().name("Mamadou")
                                .surname("Urghabananandi")
                                .hourlyRate("40.00")
                                .nip("5566557755")
                                .build());
        doctorsList.add(new DoctorDTOBuilder().name("C").surname("J").hourlyRate("123.45").nip("1122334455").build());

    }

    @Test
    void test_mappings() {
        DoctorDTO dto = doctorMapper.toDto(mostowiak);
        assertEquals(mostowiak, doctorMapper.toEntity(dto));
    }

    @Test
    void getById_whenValidId_returnsDTOfromOptional() throws NotFoundException {
        Long id = 1L;
        Optional<Doctor> expected = Optional.of(mostowiak);
        given(doctorRepository.findById(id)).willReturn(expected);
        DoctorDTO result = doctorService.getDto(id);
        assertEquals(doctorMapper.toDto(expected.get()), result);
    }

    @Test
    void getById_whenNoEntityWithGivenId_throwsEntityNotFoundException() {
        Long id = 1L;

        given(doctorRepository.findById(id)).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> doctorService.getDto(id));
    }

    @Test
    void findAll_ifEntitiesFound_returnsPageWithListOfDTOs() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Doctor> entitiesDoctorsList = doctorsList.stream()
                .map(doctorMapper::toEntity)
                .collect(Collectors.toList());
        Page<Doctor> repoResult = new PageImpl<>(entitiesDoctorsList, pageable, entitiesDoctorsList.size());

        given(doctorRepository.findAll(pageable)).willReturn(repoResult);

        Page<DoctorDTO> expected = new PageImpl<>(doctorsList, pageable, doctorsList.size());
        Page<DoctorDTO> result = doctorService.findAll(pageable);

        assertEquals(expected.get().collect(Collectors.toList()), result.get().collect(Collectors.toList()));
        assertEquals(expected.getPageable(), result.getPageable());
    }

    @Test
    void findAll_ifNoEntitiesFound_returnsEmptyPage() {
        //given
        Pageable pageable = PageRequest.of(0, 2);
        Page<Doctor> mockedRepoResult = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Page<DoctorDTO> expected = mockedRepoResult.map(doctorMapper::toDto);
        given(doctorRepository.findAll(pageable)).willReturn(mockedRepoResult);
        //when
        Page<DoctorDTO> result = doctorService.findAll(pageable);
        //then
        assertEquals(expected.get().collect(Collectors.toList()), result.get().collect(Collectors.toList()));
        assertEquals(expected.getPageable(), result.getPageable());
    }

    @Test
    void addNew_ifOK_returnDTO() throws DoctorAlreadyExistsException {
        given(doctorRepository.saveAndFlush(mostowiak)).willReturn(mostowiak);
        DoctorDTO expected = doctorMapper.toDto(mostowiak);
        DoctorDTO result = doctorService.addNew(doctorMapper.toDto(mostowiak));
        assertEquals(expected, result);
    }

    @Test
    void addNew_ifNipExists_throwNIPExistsException() {
        // simulation of existing Doctor with same NIP in database
        given(doctorRepository.findByNip(mostowiak.getNip())).willReturn(Collections.singletonList(mostowiak));
        assertThrows(DoctorAlreadyExistsException.class, () -> doctorService.addNew(doctorMapper.toDto(mostowiak)));
    }

    @Test
    void fire_ifEntryExistsAndIsActive_setsActiveToFalse() throws NotFoundException, DoctorNotActiveException {
        Doctor activeDoctor = mostowiak;
        activeDoctor.setActive(true);
        given(doctorRepository.findById(activeDoctor.getId())).willReturn(Optional.of(activeDoctor));

        DoctorDTO fired = doctorService.fire(activeDoctor.getId());

        assertFalse(fired.getActive());
    }

    @Test
    void fire_ifEntryExistsAndIsInactive_throwsDoctorNotActiveException() {
        Doctor fired = mostowiak;
        fired.setActive(false);

        given(doctorRepository.findById(fired.getId())).willReturn(Optional.of(fired));

        assertThrows(DoctorNotActiveException.class, () -> doctorService.fire(fired.getId()));
    }

    @Test
    void fire_ifNoEntry_throwsEntityNotFoundException() {
        given(doctorRepository.findById(1L)).willReturn(Optional.empty());
//		doctorService.fire(1L);
        assertThrows(NotFoundException.class, () -> doctorService.fire(1L));
    }

    @Test
    void addAnimalType_whenDoctorAndAnimalTypeExists_returnsTrueOnSuccess() throws NotFoundException, DoctorDoubledSpecialtyException, DoctorNotActiveException {
        Doctor mostowiakWithCats = new DoctorBuilder().name(mostowiak.getName())
                .surname(mostowiak.getSurname())
                .nip(mostowiak.getNip())
                .id(mostowiak.getId())
                .hourlyRate(mostowiak.getHourlyRate())
                .build();
        assertEquals(mostowiak, mostowiakWithCats);
        AnimalType pet = new AnimalType(1L, "Cats");

        mostowiakWithCats.addAnimalType(pet);

        given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
        given(animalTypeRepository.findById(pet.getId())).willReturn(Optional.ofNullable(pet));
        given(doctorRepository.saveAndFlush(mostowiakWithCats)).willReturn(mostowiakWithCats);

        DoctorDTO result = doctorService.addAnimalType(mostowiak.getId(), pet.getId());

        assertEquals(doctorMapper.toDto(mostowiakWithCats), result);
    }

    @Test
    void addAnimalType_whenAnimalTypeNotFound_throwsEntityNotFoundException() {
        given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.ofNullable(mostowiak));
        given(animalTypeRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> doctorService.addAnimalType(mostowiak.getId(), 1L));
    }

    @Test
    void addAnimalType_whenDoctorNotFound_throwsEntityNotFoundException() {
        given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.empty());
        AnimalType animalType = new AnimalType(1L, "dogs");
        given(animalTypeRepository.findById(animalType.getId())).willReturn(Optional.ofNullable(animalType));

        assertThrows(NotFoundException.class,
                     () -> doctorService.addAnimalType(mostowiak.getId(), animalType.getId()));
    }

    @Test
    void addAnimalType_whenDoctorHasAnimalType_throwsDoubledSpecialtyException() {
        AnimalType pet = new AnimalType(1L, "Dogs");
        Doctor dogsDoctor = mostowiak;
        dogsDoctor.addAnimalType(pet);

        given(animalTypeRepository.findById(1L)).willReturn(Optional.ofNullable(pet));
        given(doctorRepository.findById(dogsDoctor.getId())).willReturn(Optional.ofNullable(dogsDoctor));

        assertThrows(DoctorDoubledSpecialtyException.class,
                     () -> doctorService.addAnimalType(dogsDoctor.getId(), pet.getId()));
    }

    @Test
    void addAnimalType_whenDoctorIsNotActive_throwsDoctorIsNotActiveException() {
        Doctor inactive = mostowiak;
        inactive.setActive(false);
        AnimalType pet = new AnimalType(1L, "Dogs");

        given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.of(mostowiak));
        given(animalTypeRepository.findById(pet.getId())).willReturn(Optional.of(pet));

        assertThrows(DoctorNotActiveException.class, () -> doctorService.addAnimalType(mostowiak.getId(), pet.getId()));

    }

    @Test
    void addMedSpecialty_whenDoctorAndAnimalTypeExists_returnsTrueOnSuccess() throws NotFoundException, DoctorDoubledSpecialtyException, DoctorNotActiveException {
        Doctor cardioMostowiak = new DoctorBuilder()
                .name(mostowiak.getName())
                .surname(mostowiak.getSurname())
                .nip(mostowiak.getNip())
                .id(mostowiak.getId())
                .hourlyRate(mostowiak.getHourlyRate())
                .build();
        assertEquals(mostowiak, cardioMostowiak);

        MedSpecialty ms = new MedSpecialty(1L, "Cardio");
        cardioMostowiak.addMedSpecialty(ms);

        given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.of(mostowiak));
        given(medSpecialtyRepository.findById(ms.getId())).willReturn(Optional.of(ms));
        given(doctorRepository.saveAndFlush(cardioMostowiak)).willReturn(cardioMostowiak);

        DoctorDTO result = doctorService.addMedSpecialty(mostowiak.getId(), ms.getId());

        assertEquals(doctorMapper.toDto(cardioMostowiak), result);
    }

    @Test
    void addMedSpecialty_whenMedSpecialtyNotFound_throwsEntityNotFoundException() {
        // mocks no medSpecialty in database
        given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.of(mostowiak));
        given(medSpecialtyRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> doctorService.addMedSpecialty(mostowiak.getId(), 1L));
    }

    @Test
    void addMedSpecialty_whenDoctorNotFound_throwsEntityNotFoundException() {
        given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.empty());
        MedSpecialty medSpecialty = new MedSpecialty(1L, "Cardio");
        given(medSpecialtyRepository.findById(medSpecialty.getId())).willReturn(Optional.of(medSpecialty));

        assertThrows(NotFoundException.class,
                     () -> doctorService.addMedSpecialty(mostowiak.getId(), medSpecialty.getId()));
    }

    @Test
    void addMedSpecialty_whenDoctorHasMedSpecialty_throwsDoubledSpecialtyException() {
        MedSpecialty ms = new MedSpecialty(1L, "Cardio");
        Doctor cardioDoctor = mostowiak;
        cardioDoctor.addMedSpecialty(ms);

        given(medSpecialtyRepository.findById(1L)).willReturn(Optional.of(ms));
        given(doctorRepository.findById(cardioDoctor.getId())).willReturn(Optional.of(cardioDoctor));

        assertThrows(DoctorDoubledSpecialtyException.class,
                     () -> doctorService.addMedSpecialty(cardioDoctor.getId(), ms.getId()));
    }

    @Test
    void addMedSpecialty_whenDoctorIsNotActive_throwsDoctorIsNotActiveException() {
        Doctor inactive = mostowiak;
        inactive.setActive(false);
        MedSpecialty ms = new MedSpecialty(1L, "Cardio");

        given(doctorRepository.findById(mostowiak.getId())).willReturn(Optional.of(mostowiak));
        given(medSpecialtyRepository.findById(ms.getId())).willReturn(Optional.of(ms));

        assertThrows(DoctorNotActiveException.class,
                     () -> doctorService.addMedSpecialty(mostowiak.getId(), ms.getId()));

    }

    @Test
    void testMappings() {
        assertEquals(mostowiak, doctorMapper.toEntity(doctorMapper.toDto(mostowiak)));
    }

}