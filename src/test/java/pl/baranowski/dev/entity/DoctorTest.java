package pl.baranowski.dev.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.baranowski.dev.builder.DoctorBuilder;
import pl.baranowski.dev.builder.VisitBuilder;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;

class DoctorTest {

	private long mondayH10Y2100;
	AnimalType lion;
	private Doctor underTest;
	private Patient patient;
	@BeforeEach
	void setUp() throws Exception {
		mondayH10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();
		lion = new AnimalType("Lion");
		patient = new Patient(3L, "Pat", lion, 1, "Somebody", "this@is.email");
		underTest = new DoctorBuilder().name("Test").surname("Doctor").nip("1111111111").hourlyRate(new BigDecimal(1234)).build();
		underTest.addAnimalType(lion);
	}

	@Test
	void hasVisitsAtEpoch() throws NewVisitNotPossibleException, DoctorNotActiveException {
		//given
		Visit newVisit = new VisitBuilder().doctor(underTest).patient(patient).epoch(mondayH10Y2100).build();
		//when
		Boolean hasVisits = underTest.hasVisitsAtEpoch(mondayH10Y2100);
		//then
		assert(hasVisits);
	}

}
