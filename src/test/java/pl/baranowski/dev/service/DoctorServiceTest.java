package pl.baranowski.dev.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.doctor.DoctorAlreadyExistsException;
import pl.baranowski.dev.exception.doctor.DoctorDoubledSpecialtyException;
import pl.baranowski.dev.exception.doctor.DoctorNotActiveException;
import pl.baranowski.dev.mapper.DoctorMapper;
import pl.baranowski.dev.repository.AnimalTypeRepository;
import pl.baranowski.dev.repository.DoctorRepository;
import pl.baranowski.dev.repository.MedSpecialtyRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DoctorServiceTest {
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    AnimalTypeRepository animalTypeRepository;
    @Autowired
    MedSpecialtyRepository medSpecialtyRepository;
    @Autowired
    DoctorService doctorService;
    @Autowired
    DoctorMapper doctorMapper;
    private Doctor mostowiak;
    private List<DoctorDTO> doctorsList;

    @BeforeEach
    void setUp() {

        doctorsList = new ArrayList<>();

        mostowiak = doctorRepository.save(new DoctorBuilder().name("Mark")
                                                             .surname("Most-o-wiack")
                                                             .nip("1181328620")
                                                             .id(1L)
                                                             .hourlyRate(new BigDecimal(150))
                                                             .active(true)
                                                             .build());
        doctorsList.add(doctorMapper.toDto(mostowiak));

        List<Doctor> newDoctors = new ArrayList<>();
        newDoctors.add(new DoctorBuilder().name("Robert")
                                          .surname("Kubica")
                                          .hourlyRate(new BigDecimal(100000))
                                          .nip("1213141516")
                                          .build());

        newDoctors.add(new DoctorBuilder().name("Mirosław")
                                          .surname("Rosomak")
                                          .hourlyRate(new BigDecimal(100))
                                          .nip("0987654321")
                                          .build());

        newDoctors.add(new DoctorBuilder().name("Mamadou")
                                          .surname("Urghabananandi")
                                          .hourlyRate(new BigDecimal(40))
                                          .nip("5566557755")
                                          .build());

        newDoctors.add(new DoctorBuilder().name("C")
                                          .surname("J")
                                          .hourlyRate(new BigDecimal("123.45"))
                                          .nip("1122334455")
                                          .build());
        doctorsList.addAll(doctorRepository.saveAll(newDoctors)
                                           .stream()
                                           .map(doctorMapper::toDto)
                                           .collect(Collectors.toList()));
    }

    @AfterEach
    void tearDown() {
        doctorRepository.deleteAll();
        animalTypeRepository.deleteAll();
        medSpecialtyRepository.deleteAll();
    }

    @Test
    void test_mappings() {
        DoctorDTO dto = doctorMapper.toDto(mostowiak);
        assertEquals(mostowiak, doctorMapper.toEntity(dto));
    }

    @Test
    void get_whenValidId_returnsDTO() throws NotFoundException {
        DoctorDTO result = doctorService.getDTO(mostowiak.getId());
        assertEquals(doctorMapper.toDto(mostowiak), result);
    }

    @Test
    void get_whenNoEntityWithGivenId_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> doctorService.getDTO(1234L));
    }

    @Test
    void getEntity_whenValidId_returnsDTO() throws NotFoundException {
        Doctor result = doctorService.getEntity(mostowiak.getId());
        assertEquals(mostowiak, result);
    }

    @Test
    void getEntity_whenNoEntityWithGivenId_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> doctorService.getEntity(1234L));
    }

    @Test
    void findAll_ifEntitiesFound_returnsPageWithListOfDTOs() {
        Pageable pageable = PageRequest.of(1, 2);
        Page<DoctorDTO> expected = new PageImpl<>(doctorsList.subList(2, 4), pageable, pageable.getPageSize());

        Page<DoctorDTO> result = doctorService.findAll(pageable);

        assertEquals(expected.getContent(), result.getContent());
        assertEquals(expected.getPageable(), result.getPageable());
    }

    @Test
    void findAll_ifNoEntitiesFound_returnsEmptyPage() {
        //given
        Pageable pageable = PageRequest.of(0, 2);
        Page<DoctorDTO> expectedPage = new PageImpl<>(Collections.emptyList(), pageable, 2);
        //when
        doctorRepository.deleteAll();
        Page<DoctorDTO> resultPage = doctorService.findAll(pageable);
        //then
        assertEquals(expectedPage.getContent(), resultPage.getContent());
        assertEquals(expectedPage.getPageable(), resultPage.getPageable());
        assertEquals(expectedPage.getTotalElements(), resultPage.getTotalElements());
    }

    @Test
    void addNew_ifOK_returnsDTO() throws DoctorAlreadyExistsException {
        DoctorDTO newDoctor = new DoctorDTOBuilder().name("New")
                                                    .surname("Doctor")
                                                    .nip("5242106963")
                                                    .hourlyRate("1234.00")
                                                    .build();
        DoctorDTO result = doctorService.addNew(newDoctor);
        assertNotNull(result.getId());
        assertEquals(newDoctor.getName(), result.getName());
        assertEquals(newDoctor.getSurname(), result.getSurname());
        assertEquals(newDoctor.getNip(), result.getNip());
        assertEquals(newDoctor.getHourlyRate(), result.getHourlyRate());
    }

    @Test
    void addNew_ifNipExists_throwNIPExistsException() {
        assertThrows(DoctorAlreadyExistsException.class, () -> doctorService.addNew(doctorMapper.toDto(mostowiak)));
    }

    @Test
    void fire_ifEntryExistsAndIsActive_setsActiveToFalse_andReturnsInactive() throws NotFoundException, DoctorNotActiveException {
        DoctorDTO fired = doctorService.fire(mostowiak.getId());

        assertFalse(doctorRepository.findById(mostowiak.getId()).get().isActive());
        assertFalse(fired.getActive());
    }

    @Test
    void fire_ifEntryExistsAndIsInactive_throwsDoctorNotActiveException() {
        mostowiak.setActive(false);
        doctorRepository.save(mostowiak);

        assertThrows(DoctorNotActiveException.class, () -> doctorService.fire(mostowiak.getId()));
    }

    @Test
    void fire_ifNoEntry_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> doctorService.fire(123L));
    }

    @Test
    void addAnimalType_whenDoctorAndAnimalTypeExists_returnsTrueOnSuccess() throws NotFoundException, DoctorDoubledSpecialtyException, DoctorNotActiveException {
        AnimalType cats = animalTypeRepository.save(new AnimalType("Cats"));
        doctorService.addAnimalType(mostowiak.getId(), cats.getId());

        assert (doctorRepository.findById(mostowiak.getId()).get().getAnimalTypes().contains(cats));
    }

    @Test
    void addAnimalType_whenAnimalTypeNotFound_throwsEntityNotFoundException() {
        assertThrows(NotFoundException.class, () -> doctorService.addAnimalType(mostowiak.getId(), 1000L));
    }

    @Test
    void addAnimalType_whenDoctorNotFound_throwsEntityNotFoundException() {
        AnimalType animalType = animalTypeRepository.save(new AnimalType("Dogs"));
        assertThrows(NotFoundException.class,
                     () -> doctorService.addAnimalType(444L, animalType.getId()));
    }

    @Test
    void addAnimalType_whenDoctorHasAnimalType_throwsDoubledSpecialtyException() {
        AnimalType animalType = animalTypeRepository.save(new AnimalType("Dogs"));
        mostowiak.addAnimalType(animalType);
        doctorRepository.save(mostowiak);

        assertThrows(DoctorDoubledSpecialtyException.class,
                     () -> doctorService.addAnimalType(mostowiak.getId(), animalType.getId()));
    }

    @Test
    void addAnimalType_whenDoctorIsNotActive_throwsDoctorIsNotActiveException() {
        mostowiak.setActive(false);
        doctorRepository.save(mostowiak);

        AnimalType animalType = animalTypeRepository.save(new AnimalType("Dogs"));

        assertThrows(DoctorNotActiveException.class,
                     () -> doctorService.addAnimalType(mostowiak.getId(), animalType.getId()));
    }

    @Test
    void addMedSpecialty_whenDoctorAndAnimalTypeExists_returnsTrueOnSuccess() throws NotFoundException, DoctorDoubledSpecialtyException, DoctorNotActiveException {
        MedSpecialty medSpecialty = medSpecialtyRepository.save(new MedSpecialty("Cardio"));

        doctorService.addMedSpecialty(mostowiak.getId(), medSpecialty.getId());

        assertTrue(doctorRepository.findById(mostowiak.getId()).get().getMedSpecialties().contains(medSpecialty));
    }

    @Test
    void addMedSpecialty_whenMedSpecialtyNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> doctorService.addMedSpecialty(mostowiak.getId(), 123L));
    }

    @Test
    void addMedSpecialty_whenDoctorNotFound_throwsNotFoundException() {
        MedSpecialty medSpecialty = medSpecialtyRepository.save(new MedSpecialty("Cardio"));
        assertThrows(NotFoundException.class,
                     () -> doctorService.addMedSpecialty(1234L, medSpecialty.getId()));
    }

    @Test
    void addMedSpecialty_whenDoctorHasMedSpecialty_throwsDoubledSpecialtyException() throws DoctorDoubledSpecialtyException, NotFoundException, DoctorNotActiveException {
        MedSpecialty medSpecialty = medSpecialtyRepository.save(new MedSpecialty("Cardio"));
        doctorService.addMedSpecialty(mostowiak.getId(), medSpecialty.getId());
        assertThrows(DoctorDoubledSpecialtyException.class,
                     () -> doctorService.addMedSpecialty(mostowiak.getId(), medSpecialty.getId()));
    }

    @Test
    void addMedSpecialty_whenDoctorIsNotActive_throwsDoctorIsNotActiveException() throws DoctorNotActiveException, NotFoundException {
        MedSpecialty medSpecialty = medSpecialtyRepository.save(new MedSpecialty("Cardio"));
        doctorService.fire(mostowiak.getId());
        assertThrows(DoctorNotActiveException.class,
                     () -> doctorService.addMedSpecialty(mostowiak.getId(), medSpecialty.getId()));

    }
}
