package pl.baranowski.dev.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.exception.InvalidEpochTimeException;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;

@SpringBootTest
class AvailableSlotsFinderTest {

	private AnimalType cat;
	private Patient patient;
	private Doctor neurologist;
	private Doctor cardiologist;
	private List<Doctor> doctors;
	private EpochFutureTimeRange timeRange;
	private AvailableSlotsFinder underTest;
	private final Long MONDAY_10 = 1894010400L;
	private final Long MONDAY_11 = MONDAY_10 + 3600;
	private final Long MONDAY_12 = MONDAY_11 + 3600;
	private final Long MONDAY_13 = MONDAY_12 + 3600;
	private final long MONDAY_H10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();


	@BeforeEach
	public void init() throws NewVisitNotPossibleException, DoctorNotActiveException {
		doctors = new ArrayList<>();
		cat = new AnimalType("Cat");
		patient = new Patient("Luis", cat, 12, "Lionel Messi", "me@ss.i");

		neurologist = new Doctor.Builder("John", "Wayne", new BigDecimal(40), "1111111111").build();
		neurologist.addAnimalType(cat);
		neurologist.addVisit(new Visit.VisitBuilder(neurologist, patient, MONDAY_11).build());
		neurologist.addVisit(new Visit.VisitBuilder(neurologist, patient, MONDAY_12).build());
		doctors.add(neurologist);
		
		cardiologist = new Doctor.Builder("Max", "Payne", new BigDecimal(140), "1181328620").build();
		cardiologist.addAnimalType(cat);
		cardiologist.addVisit(new Visit.VisitBuilder(cardiologist, patient, MONDAY_10).build());
		cardiologist.addVisit(new Visit.VisitBuilder(cardiologist, patient, MONDAY_13).build());
		doctors.add(cardiologist);

	}
	
	@Test
	void find_shouldReturnTwoDoctorsWithCorrectSlots() throws InvalidEpochTimeException {
		//given
		timeRange = new EpochFutureTimeRange(MONDAY_10, MONDAY_13);
		underTest = new AvailableSlotsFinder(doctors, timeRange);
		//when
		List<AvailableSlotsAtTheDoctor> actual = underTest.find();
		//then
		List<AvailableSlotsAtTheDoctor> expected = new ArrayList<>();
		expected.add(new AvailableSlotsAtTheDoctor(neurologist, Arrays.asList(MONDAY_10, MONDAY_13)));
		expected.add(new AvailableSlotsAtTheDoctor(cardiologist, Arrays.asList(MONDAY_11, MONDAY_12)));
		assertEquals(expected, actual);
	}

	@Test
	void topHoursBetween_validInput() throws InvalidEpochTimeException {
		underTest = new AvailableSlotsFinder(doctors, timeRange);
		long start = MONDAY_H10Y2100 + 30*60; // 2100-01-25 10:30:00;
		long end = MONDAY_H10Y2100 + 24*60*60 - 30*60; // 2100-01-26 9:30:00;
		EpochFutureTimeRange timeRange = new EpochFutureTimeRange(start, end);
		assertEquals(23, underTest.topHoursBetween(timeRange).size());
	}

	@Test
	void topHoursBetween_startAfterEnd_shouldThrow() throws InvalidEpochTimeException {
		underTest = new AvailableSlotsFinder(doctors, timeRange);
		long start = MONDAY_H10Y2100 + 30*60; // 2100-01-25 10:30:00;
		long end = MONDAY_H10Y2100 + 24*60*60 - 30*60; // 2100-01-26 9:30:00;
		assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(end, start));
	}

	@Test
	void topHoursBetween_startNotInSeconds_shouldThrow() throws InvalidEpochTimeException {
		underTest = new AvailableSlotsFinder(doctors, timeRange);
		long start = MONDAY_H10Y2100 + 30*60; // 2100-01-25 10:30:00;
		long end = MONDAY_H10Y2100 + 24*60*60 - 30*60; // 2100-01-26 9:30:00;
		assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(start*10, end));
	}

	@Test
	void topHoursBetween_endNotInSeconds_shouldThrow() throws InvalidEpochTimeException {
		underTest = new AvailableSlotsFinder(doctors, timeRange);
		long start = MONDAY_H10Y2100 + 30*60; // 2100-01-25 10:30:00;
		long end = MONDAY_H10Y2100 + 24*60*60 - 30*60; // 2100-01-26 9:30:00;
		assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(start, end*10));
	}

	@Test
	void topHoursBetween_startBeforeNow_shouldThrow() throws InvalidEpochTimeException {
		underTest = new AvailableSlotsFinder(doctors, timeRange);
		long now = System.currentTimeMillis() / 1000;
		long end = MONDAY_H10Y2100 + 24*60*60 - 30*60; // 2100-01-26 9:30:00;
		assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(now - 1, end));
	}

}
