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
import pl.baranowski.dev.builder.VisitBuilder;
import pl.baranowski.dev.dto.DoctorsFreeSlotsDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.*;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.doctor.DoctorNotActiveException;
import pl.baranowski.dev.exception.epoch.InvalidEpochTimeException;
import pl.baranowski.dev.exception.visit.NewVisitNotPossibleException;
import pl.baranowski.dev.mapper.VisitMapper;
import pl.baranowski.dev.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class VisitServiceTest {
    private final long MONDAY_H10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 0, 0),
                                                          ZoneId.systemDefault())
                                                      .toEpochSecond();
    private final long MONDAY_H00Y2100 = MONDAY_H10Y2100 - 10 * 3600;
    @Autowired
    AnimalTypeRepository animalTypeRepository;
    @Autowired
    MedSpecialtyRepository medSpecialtyRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    VisitRepository visitRepository;
    @Autowired
    VisitService visitService;
    @Autowired
    VisitMapper mapper;
    private AnimalType animalType;
    private MedSpecialty medSpecialty;
    private Doctor doctor;
    private Patient patient;
    private Visit visit;

    @BeforeEach
    void setUp() {
        AnimalType newAnimalType = new AnimalType("Owad");
        this.animalType = animalTypeRepository.save(newAnimalType);

        MedSpecialty newMedSpecialty = new MedSpecialty("Czółkolog");
        this.medSpecialty = medSpecialtyRepository.save(newMedSpecialty);

        Doctor newDoctor = new DoctorBuilder().name("Kazik")
                                              .surname("Montana")
                                              .nip("1111111111")
                                              .hourlyRate(new BigDecimal(220))
                                              .build();
        newDoctor.addAnimalType(animalType);
        newDoctor.addMedSpecialty(medSpecialty);
        doctor = doctorRepository.save(newDoctor);

        patient = new Patient("Karaluch", animalType, 13, "Lubiacz Owadów", "ijegomail@sld.pl");
        patientRepository.save(patient);

        Visit newVisit = new VisitBuilder().doctor(doctor).patient(patient).epoch(MONDAY_H10Y2100).build();
        visit = visitRepository.save(newVisit);
    }

    @AfterEach
    void tearDown() {
        visitRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
        medSpecialtyRepository.deleteAll();
        animalTypeRepository.deleteAll();
    }

    @Test
    void getById_whenEntityFound_returnsDTO() throws NotFoundException {
        VisitDTO expectedDTO = mapper.toDto(visit);

        VisitDTO resultDTO = visitService.getById(visit.getId());

        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void getById_whenEntityNotFound_throwsNotFoundException() {
        long noEntityId = 100L;
        assertThrows(NotFoundException.class, () -> visitService.getById(noEntityId));
    }


    @Test
    void findAll_whenValidParams_returnsPageOfDTOs() {
        Pageable pageable = PageRequest.of(0, 5);
        List<VisitDTO> visitDTOs = visitRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
        Page<VisitDTO> expectedPage = new PageImpl<>(visitDTOs, pageable, visitDTOs.size());

        Page<VisitDTO> resultPage = visitService.findAll(pageable);

        assertEquals(expectedPage.getTotalPages(), resultPage.getTotalPages());
        assertEquals(expectedPage.getTotalElements(), resultPage.getTotalElements());
        assertEquals(expectedPage.getPageable(), resultPage.getPageable());
//        assertEquals(expectedPage.getContent(), resultPage.getContent());
    }

    @Test
    void findAll_whenNoEntities_emptyPageReturnValue() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<VisitDTO> expectedEmptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        visitRepository.deleteAll();
        Page<VisitDTO> resultPage = visitService.findAll(pageable);

        assertEquals(expectedEmptyPage.getTotalPages(), resultPage.getTotalPages());
        assertEquals(expectedEmptyPage.getTotalElements(), resultPage.getTotalElements());
        assertEquals(expectedEmptyPage.getPageable(), resultPage.getPageable());
        assertEquals(expectedEmptyPage.getContent(), resultPage.getContent());
    }


    @Test
    void addNew_whenValidParams_returnsDTO() throws Exception {
        //given
        long mondayH11Y2100 = MONDAY_H10Y2100 + 3600;
        Visit newVisit = new VisitBuilder()
                .doctor(visit.getDoctor())
                .patient(visit.getPatient())
                .epoch(mondayH11Y2100)
                .build();

        // verifies return value
        VisitDTO expected = mapper.toDto(newVisit);
        VisitDTO result = visitService.addNew(newVisit.getDoctor().getId(),
                                              newVisit.getPatient().getId(),
                                              newVisit.getEpoch());
        assertEquals(expected.getDoctor(), result.getDoctor());
        assertEquals(expected.getPatient(), result.getPatient());
        assertEquals(expected.getEpoch(), result.getEpoch());
        assertEquals(expected.getConfirmed(), result.getConfirmed());
        assertEquals(expected.getDuration(), result.getDuration());
        assertNotNull(result.getId());
        assertFalse(result.getConfirmed());
    }

    @Test
    void addNew_whenNoDoctorOrPatient_throwsNotFoundException() {
        Long mondayH12Y2100 = MONDAY_H10Y2100 + 2 * 3600;

        Long noDoctorId = 1234L;
        assertThrows(NotFoundException.class, () -> visitService.addNew(noDoctorId, patient.getId(), mondayH12Y2100));
        Long noPatientId = 1234L;
        assertThrows(NotFoundException.class, () -> visitService.addNew(doctor.getId(), noPatientId, mondayH12Y2100));
    }

    @Test
    void addNew_whenDoctorOrPatientHasAlreadyVisitAtEpoch_throwsNewVisitNotPossibleException() {
        // given new entities
        Doctor newDoctorJohn = new DoctorBuilder().name("John")
                                                  .surname("Scott")
                                                  .nip("1111111111")
                                                  .hourlyRate(new BigDecimal(456))
                                                  .build();
        newDoctorJohn.addAnimalType(animalType);
        Doctor doctorJohn = doctorRepository.save(newDoctorJohn);

        Patient newPatientRon = new Patient("Ron", animalType, 123, "Harry P.", "i@like.sl");
        Patient patientRon = patientRepository.save(newPatientRon);
        System.out.println("Patient Ron: " + patientRon);
        assertThrows(NewVisitNotPossibleException.class,
                     () -> visitService.addNew(doctor.getId(), patientRon.getId(), MONDAY_H10Y2100));

        assertThrows(NewVisitNotPossibleException.class,
                     () -> visitService.addNew(doctorJohn.getId(), patient.getId(), MONDAY_H10Y2100));
    }

    @Test
    void addNew_whenDoctorIsNotActive_throwsDoctorNotActiveException() {
        Doctor newInactiveDoctor = new DoctorBuilder().name("Mały")
                                                      .surname("Zenek")
                                                      .nip("1111111111")
                                                      .hourlyRate(new BigDecimal(100))
                                                      .active(false)
                                                      .build();
        Doctor inactiveDoctor = doctorRepository.save(newInactiveDoctor);
        Long mondayH13Y2100 = MONDAY_H10Y2100 + 3 * 3600;

        assertThrows(DoctorNotActiveException.class,
                     () -> visitService.addNew(inactiveDoctor.getId(), patient.getId(), mondayH13Y2100));
    }

    @Test
    void addNew_whenDoctorDoesNotHavePatientsAnimalType_throwsNewVisitNotPossibleException() {
        AnimalType newCats = new AnimalType("Cats");
        AnimalType cats = animalTypeRepository.save(newCats);

        Doctor newCatsDoctor = new DoctorBuilder().name("Mały")
                                                  .surname("Zenek")
                                                  .nip("1111111111")
                                                  .hourlyRate(new BigDecimal(100))
                                                  .build();
        newCatsDoctor.addAnimalType(cats);
        Doctor catsDoctor = doctorRepository.save(newCatsDoctor);

        Long mondayH14Y2100 = MONDAY_H10Y2100 + 4 * 3600;
        assertThrows(NewVisitNotPossibleException.class,
                     () -> visitService.addNew(catsDoctor.getId(), patient.getId(), mondayH14Y2100));
    }

    @Test
    void addNew_whenEpochNotInFuture_throwsNewVisitNotPossibleException() {
        assertThrows(NewVisitNotPossibleException.class,
                     () -> visitService.addNew(doctor.getId(),
                                               patient.getId(),
                                               System.currentTimeMillis() / 1000 - 60)); // now - 1 minute
    }

    @Test
    void addNew_whenEpochIsOutsideDoctorsWorkingHoursOrDays_throwsNewVisitNotPossibleException() {
        long epochMondayBeforeWork = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 8, 0, 0), ZoneId.systemDefault())
                                                  .toEpochSecond();
        assertThrows(NewVisitNotPossibleException.class,
                     () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayBeforeWork));

        long epochMondayAfterWork = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 16, 0, 0), ZoneId.systemDefault())
                                                 .toEpochSecond();
        assertThrows(NewVisitNotPossibleException.class,
                     () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayAfterWork));

        long epochSunday = ZonedDateTime.of(LocalDateTime.of(2100, 1, 31, 12, 0, 0), ZoneId.systemDefault())
                                        .toEpochSecond();
        assertThrows(NewVisitNotPossibleException.class,
                     () -> visitService.addNew(doctor.getId(), patient.getId(), epochSunday));
    }

    @Test
    void addNew_whenEpochIsNotAtTheTopOfTheHour_throwsNewVisitNotPossibleException() {
        // 1sec after the top of the hour
        long epochMondayDuringWorkPlus1s = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 15, 0, 1),
                                                            ZoneId.systemDefault()).toEpochSecond();
        assertThrows(NewVisitNotPossibleException.class,
                     () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayDuringWorkPlus1s));

        // 2min after the top of the hour
        long epochMondayDuringWorkPlus2min = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 15, 2, 0),
                                                              ZoneId.systemDefault()).toEpochSecond();
        assertThrows(NewVisitNotPossibleException.class,
                     () -> visitService.addNew(doctor.getId(), patient.getId(), epochMondayDuringWorkPlus2min));
    }

    @Test
    void findAvailableVisits_whenValidInput_omitsDoctorsBeforeWorkingHours() throws NotFoundException, InvalidEpochTimeException {
        //given
        Long oneHour = 60 * 60L;
        Long atWorkBeginning = MONDAY_H00Y2100 + doctor.getWorksFromHour() * oneHour;
        Long oneHourBeforeWorkBeginning = atWorkBeginning - oneHour;
        Long oneHourAfterWorkBeginning = atWorkBeginning + oneHour;
        //when
        List<DoctorsFreeSlotsDTO> result = visitService.findAvailableVisits(animalType.getName(),
                                                                            medSpecialty.getName(),
                                                                            oneHourBeforeWorkBeginning,
                                                                            oneHourAfterWorkBeginning);
        //then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getAvailableEpochTimes().size());
        assertEquals(atWorkBeginning, result.get(0).getAvailableEpochTimes().get(0));
    }

    @Test
    void findAvailableVisits_whenValidInput_omitsDoctorsAfterWorkingHours() throws NotFoundException, InvalidEpochTimeException {
        //given
        Long oneHour = 60 * 60L;
        Long atWorkEnding = MONDAY_H00Y2100 + doctor.getWorksTillHour() * oneHour;
        Long oneHourBeforeWorkEnding = atWorkEnding - oneHour;
        Long oneHourAfterWorkEnding = atWorkEnding + oneHour;
        //when
        List<DoctorsFreeSlotsDTO> result = visitService.findAvailableVisits(animalType.getName(),
                                                                            medSpecialty.getName(),
                                                                            oneHourBeforeWorkEnding,
                                                                            oneHourAfterWorkEnding);
        //then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getAvailableEpochTimes().size());
        assertEquals(oneHourBeforeWorkEnding, result.get(0).getAvailableEpochTimes().get(0));
    }

    @Test
    void findAvailableVisits_whenValidInput_omitsDoctorsNonWorkingDays() throws NotFoundException, InvalidEpochTimeException {
        //given
        Long oneHour = 60 * 60L;
        Long sundayH09 = MONDAY_H00Y2100 - (24 * oneHour) + (9 * oneHour);
        Long sundayH15 = sundayH09 + (6 * oneHour);
        //when
        List<DoctorsFreeSlotsDTO> result = visitService.findAvailableVisits(animalType.getName(),
                                                                            medSpecialty.getName(),
                                                                            sundayH09,
                                                                            sundayH15);
        //then
        assertEquals(0, result.size());
    }

    @Test
    void findAvailableVisits_whenValidInput_omitsInactiveDoctors() throws NotFoundException, InvalidEpochTimeException {
        //given
        Long oneHour = 60 * 60L;
        Long atWorkBeginning = MONDAY_H00Y2100 + doctor.getWorksFromHour() * oneHour;
        Long sixHoursAfterWorkBeginning = atWorkBeginning + 6 * oneHour;
        doctor.setActive(false);
        doctorRepository.save(doctor);
        //when
        List<DoctorsFreeSlotsDTO> result = visitService.findAvailableVisits(animalType.getName(),
                                                                            medSpecialty.getName(),
                                                                            atWorkBeginning,
                                                                            sixHoursAfterWorkBeginning);
        //then
        assertEquals(0, result.size());
    }

    @Test
    void findAvailableVisits_whenValidInput_omitsBusyDoctorsSlots() throws NewVisitNotPossibleException, NotFoundException, DoctorNotActiveException, InvalidEpochTimeException {
        //given
        Long oneHour = 60 * 60L;
        Long atWorkBeginning = MONDAY_H00Y2100 + doctor.getWorksFromHour() * oneHour;
        Long sixHoursAfterWorkBeginning = atWorkBeginning + 6 * oneHour;
        visitService.addNew(doctor.getId(), patient.getId(), atWorkBeginning);
        visitService.addNew(doctor.getId(), patient.getId(), sixHoursAfterWorkBeginning);
        //when
        List<DoctorsFreeSlotsDTO> result = visitService.findAvailableVisits(animalType.getName(),
                                                                            medSpecialty.getName(),
                                                                            atWorkBeginning,
                                                                            sixHoursAfterWorkBeginning);
        //then
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getAvailableEpochTimes().size());
    }

    @Test
    void findAvailableVisits_whenAnimalTypeNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class,
                     () -> visitService.findAvailableVisits("ęęę",
                                                            medSpecialty.getName(),
                                                            MONDAY_H00Y2100,
                                                            MONDAY_H10Y2100));
    }

    @Test
    void findAvailableVisits_whenMedSpecialtyNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class,
                     () -> visitService.findAvailableVisits(animalType.getName(),
                                                            "ąąą",
                                                            MONDAY_H00Y2100,
                                                            MONDAY_H10Y2100));
    }

    @Test
    void findAvailableVisits_whenEpochStartBeforeNow_throwsInvalidEpochTimeException() {
        assertThrows(InvalidEpochTimeException.class, () -> visitService.findAvailableVisits(animalType.getName(),
                                                                                             medSpecialty.getName(),
                                                                                             System.currentTimeMillis() - 3600,
                                                                                             MONDAY_H10Y2100));
    }

    @Test
    void findAvailableVisits_whenEpochStartAfterEpochEnd_throwsInvalidEpochTimeException() {
        assertThrows(InvalidEpochTimeException.class, () -> visitService.findAvailableVisits(animalType.getName(),
                                                                                             medSpecialty.getName(),
                                                                                             MONDAY_H10Y2100,
                                                                                             MONDAY_H00Y2100));
    }
}