package pl.baranowski.dev.manager;

import pl.baranowski.dev.builder.VisitBuilder;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.service.DoctorService;
import pl.baranowski.dev.service.PatientService;

public class Reception {
	private final DoctorService doctorService;
	private final PatientService patientService;

	public Reception(DoctorService doctorService, PatientService patientService) {
		this.doctorService = doctorService;
		this.patientService = patientService;
	}

	public Visit createNewVisitIfPossible(Long doctorId, Long patientId, Long epochInSeconds) throws Exception {
		Doctor doctor = doctorService.get(doctorId);
		Patient patient = patientService.get(patientId);

		Visit visit = new VisitBuilder().doctor(doctor).patient(patient).epoch(epochInSeconds).build();
		validateVisit(visit);
		
		return visit;
	}

	private void validateVisit(Visit visit) throws NewVisitNotPossibleException, DoctorNotActiveException {
		validateEpoch(visit.getEpoch());
		
		validateDoctor(visit.getDoctor());
		validateDoctorAvailability(visit);
		
		validatePatientAvailability(visit);
		
		validateAnimalTypeMatching(visit.getDoctor(), visit.getPatient());
		
	}

	private void validateEpoch(Long epochInSeconds) throws NewVisitNotPossibleException {
		throwIfEpochIsNotInFuture(epochInSeconds);
		throwIfEpochIsNotAtTheTopOfTheHour(epochInSeconds);
	}

	private void throwIfEpochIsNotInFuture(Long epochInSeconds) throws NewVisitNotPossibleException {
		if(epochInSeconds <= System.currentTimeMillis()/1000) {
			throw new NewVisitNotPossibleException("Creating new Visit failed: provided epoch time is not in the future.");
		}
	}

	private void throwIfEpochIsNotAtTheTopOfTheHour(Long epochInSeconds) throws NewVisitNotPossibleException {
		if(epochInSeconds % 3600 != 0) {
			throw new NewVisitNotPossibleException("Time should be at exact hour (at the top of the hour).");
		}
	}

	private void validateDoctor(Doctor doctor) throws DoctorNotActiveException {
		throwIfDoctorIsInactive(doctor);
	}
	
	private void throwIfDoctorIsInactive(Doctor doctor) throws DoctorNotActiveException {
		if(!doctor.isActive()) {
			throw new DoctorNotActiveException("Creating Visit failed. Doctor with id " + doctor.getId() + " is not active.");
		}
	}
	
	private void validateDoctorAvailability(Visit visit) throws NewVisitNotPossibleException {
		throwIfDoctorIsBusyAtEpoch(visit.getDoctor(), visit.getEpoch());
		throwIfDoctorDoesNotWorkAtEpoch(visit.getDoctor(), visit.getEpoch());
		throwIfDoctorEndsWorkBeforeVisitEnds(visit);
		
	}
	
	private void throwIfDoctorIsBusyAtEpoch(Doctor doctor, Long epochInSeconds) throws NewVisitNotPossibleException {
		if(doctor.hasVisitsAtEpoch(epochInSeconds)) {
			throw new NewVisitNotPossibleException("Doctor with id " + doctor.getId() + " is busy at provided time.");
		}
	}

	private void throwIfDoctorDoesNotWorkAtEpoch(Doctor doctor, Long epochInSeconds) throws NewVisitNotPossibleException {
		if(!doctor.worksAt(epochInSeconds)) {
			throw new NewVisitNotPossibleException("Doctor with id " + doctor.getId() + " does not work at given time.");
		}
	}
	
	private void throwIfDoctorEndsWorkBeforeVisitEnds(Visit visit) throws NewVisitNotPossibleException {
		Doctor doctor = visit.getDoctor();
		if(!doctor.worksAt(visit.getEpoch() + visit.getDuration())) {
			throw new NewVisitNotPossibleException("Doctor with id " + doctor.getId() + " ends work before visit ends.");
		}
	}

	private void validatePatientAvailability(Visit visit) throws NewVisitNotPossibleException {
		throwIfPatientBusyAtEpoch(visit);
	}
	
	/*
	 * Checks, if Patient has any visits at epoch.
	 * Unconfirmed visits are also considered.
	 */
	private void throwIfPatientBusyAtEpoch(Visit visit) throws NewVisitNotPossibleException {
		if(visit.getPatient().hasVisitsAt(visit.getEpoch())) {
			throw new NewVisitNotPossibleException("Patient has another visit at this time.");
		}
	}

	private void validateAnimalTypeMatching(Doctor doctor, Patient patient) throws NewVisitNotPossibleException {
		if(!animalTypeMatches(doctor, patient)) {
			throw new NewVisitNotPossibleException("Patient's animal type does not match Doctor's animal types");
		}
	}

	private boolean animalTypeMatches(Doctor doctor, Patient patient) {
		return doctor.getAnimalTypes().contains(patient.getAnimalType());
	}
}
