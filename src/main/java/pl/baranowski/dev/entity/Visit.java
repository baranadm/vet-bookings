package pl.baranowski.dev.entity;

import pl.baranowski.dev.builder.VisitBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Visit {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "doctor_id")
	private Doctor doctor;
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;
	private long epochInSeconds;
	private long duration;
	private Boolean isConfirmed;

	public Visit() {
	}

	public Visit(Long id, Doctor doctor, Patient patient, long epochInSeconds, long duration, Boolean isConfirmed) {
		this.id = id;
		this.doctor = doctor;
		this.patient = patient;
		this.epochInSeconds = epochInSeconds;
		this.duration = duration;
		this.isConfirmed = isConfirmed;
	}

	public static VisitBuilder builder() {
		return new VisitBuilder();
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
