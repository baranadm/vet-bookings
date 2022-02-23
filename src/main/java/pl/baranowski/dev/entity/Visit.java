package pl.baranowski.dev.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import pl.baranowski.dev.exception.DoctorNotActiveException;
import pl.baranowski.dev.exception.NewVisitNotPossibleException;

@Entity
public class Visit {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "doctor_id")
	private Doctor doctor; // required
	
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient; // required
	
	private long epochInSeconds; // required
	private long duration = 3600; // optional, default = 3600
	private Boolean isConfirmed = false; // optional, default = false
	
	public Visit() {
	}
	
	private Visit(VisitBuilder visitBuilder) throws NewVisitNotPossibleException, DoctorNotActiveException {
		this.doctor = visitBuilder.doctor;
		this.patient = visitBuilder.patient;
		this.epochInSeconds = visitBuilder.epoch;
		this.duration = visitBuilder.duration;
		this.isConfirmed = visitBuilder.isConfirmed;
		validateVisit();
		doctor.addVisit(this);
		patient.addVisit(this);
	}

	private void validateVisit() throws NewVisitNotPossibleException, DoctorNotActiveException {
		validateEpoch();
		validateDoctor();
		validatePatient();
		validateAnimalTypeMatching();
		
	}

	private void validateEpoch() throws NewVisitNotPossibleException {
		throwIfEpochIsNotInFuture();
		throwIfEpochIsNotAtTheTopOfTheHour();
	}

	private void throwIfEpochIsNotInFuture() throws NewVisitNotPossibleException {
		if(epochInSeconds - System.currentTimeMillis()/1000 < 0) {
			throw new NewVisitNotPossibleException("Creating new Visit failed: provided epoch time is not in the future.");
		}
	}

	private void throwIfEpochIsNotAtTheTopOfTheHour() throws NewVisitNotPossibleException {
		if(epochInSeconds % 3600 != 0) {
			throw new NewVisitNotPossibleException("Time should be at exact hour (at the top of the hour).");
		}
	}

	private void validateDoctor() throws DoctorNotActiveException, NewVisitNotPossibleException {
		throwIfDoctorIsInactive();
		throwIfDoctorIsBusyAtEpoch();
		throwIfDoctorDoesNotWorkAtEpoch();
	}
	
	private void throwIfDoctorIsInactive() throws DoctorNotActiveException {
		if(!doctor.isActive()) {
			throw new DoctorNotActiveException("Creating Visit failed. Doctor with id " + doctor.getId() + " is not active.");
		}
	}
	
	private void throwIfDoctorIsBusyAtEpoch() throws NewVisitNotPossibleException {
		if(doctor.hasVisitsAtEpoch(epochInSeconds)) {
			throw new NewVisitNotPossibleException("Doctor with id " + doctor.getId() + " is busy at provided time.");
		}
	}

	private void throwIfDoctorDoesNotWorkAtEpoch() throws NewVisitNotPossibleException {
		if(!doctor.worksAt(epochInSeconds)) {
			throw new NewVisitNotPossibleException("Doctor with id " + doctor.getId() + " does not work at given time.");
		}
	}

	private void validatePatient() throws NewVisitNotPossibleException {
		throwIfPatientBusyAtEpoch();
	}
	
	/*
	 * Checks, if Patient has any visits at epoch.
	 * Unconfirmed visits are also considered.
	 */
	private void throwIfPatientBusyAtEpoch() throws NewVisitNotPossibleException {
		if(patient.hasVisitsAt(epochInSeconds)) {
			throw new NewVisitNotPossibleException("Patient has another visit at this time.");
		}
	}

	private void validateAnimalTypeMatching() throws NewVisitNotPossibleException {
		if(!animalTypeMatches()) {
			throw new NewVisitNotPossibleException("Patient's animal type does not match Doctor's animal types");
		}
	}

	private boolean animalTypeMatches() {
		return doctor.getAnimalTypes().contains(patient.getAnimalType());
	}
	
	public Long getId() {
		return id;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public Patient getPatient() {
		return patient;
	}

	public long getEpoch() {
		return epochInSeconds;
	}
	
	public long getDuration() {
		return duration;
	}

	public Boolean getIsConfirmed() {
		return isConfirmed;
	}
	
	public Visit withId(long id) {
		this.id = id;
		return this;
	}
	
	public static class VisitBuilder {
		private final Doctor doctor; // required
		private final Patient patient; // required
		private final long epoch; // required
		private long duration = 3600; // optional, default = 3600
		private Boolean isConfirmed = false; // optional, default = false
		
		public VisitBuilder(Doctor doctor, Patient patient, long epoch) {
			this.doctor = doctor;
			this.patient = patient;
			this.epoch = epoch;
		}
		
		public VisitBuilder duration(long duration) {
			this.duration = duration;
			return this;
		}
		
		public VisitBuilder isConfirmed(boolean isConfirmed) {
			this.isConfirmed = isConfirmed;
			return this;
		}
		
		public Visit build() throws NewVisitNotPossibleException, DoctorNotActiveException {
			return new Visit(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (duration ^ (duration >>> 32));
		result = prime * result + (int) (epochInSeconds ^ (epochInSeconds >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isConfirmed == null) ? 0 : isConfirmed.hashCode());
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
		result = prime * result + ((doctor == null) ? 0 : doctor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Visit other = (Visit) obj;
		if (duration != other.duration)
			return false;
		if (epochInSeconds != other.epochInSeconds)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isConfirmed == null) {
			if (other.isConfirmed != null)
				return false;
		} else if (!isConfirmed.equals(other.isConfirmed))
			return false;
		if (patient == null) {
			if (other.patient != null)
				return false;
		} else if (!patient.equals(other.patient))
			return false;
		if (doctor == null) {
			if (other.doctor != null)
				return false;
		} else if (!doctor.equals(other.doctor))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Visit [id=" + id + ", doctor=" + doctor + ", patient=" + patient + ", epoch=" + epochInSeconds + ", duration="
				+ duration + ", isConfirmed=" + isConfirmed + "]";
	}
	
}
