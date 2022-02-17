package pl.baranowski.dev.entity;

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
	@JoinColumn(name = "vet_id")
	private Vet vet; // required
	
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient; // required
	
	private long epoch; // required
	private long duration = 3600; // optional, default = 3600
	private Boolean isConfirmed = false; // optional, default = false
	
	public Visit() {
	}
	
	private Visit(VisitBuilder visitBuilder) {
		this.vet = visitBuilder.vet;
		this.patient = visitBuilder.patient;
		this.epoch = visitBuilder.epoch;
		this.duration = visitBuilder.duration;
		this.isConfirmed = visitBuilder.isConfirmed;
	}

	public Long getId() {
		return id;
	}

	public Vet getVet() {
		return vet;
	}

	public Patient getPatient() {
		return patient;
	}

	public long getEpoch() {
		return epoch;
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
		private final Vet vet; // required
		private final Patient patient; // required
		private final long epoch; // required
		private long duration = 3600; // optional, default = 3600
		private Boolean isConfirmed = false; // optional, default = false
		
		public VisitBuilder(Vet vet, Patient patient, long epoch) {
			this.vet = vet;
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
		
		public Visit build() {
			return new Visit(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (duration ^ (duration >>> 32));
		result = prime * result + (int) (epoch ^ (epoch >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isConfirmed == null) ? 0 : isConfirmed.hashCode());
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
		result = prime * result + ((vet == null) ? 0 : vet.hashCode());
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
		if (epoch != other.epoch)
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
		if (vet == null) {
			if (other.vet != null)
				return false;
		} else if (!vet.equals(other.vet))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Visit [id=" + id + ", vet=" + vet + ", patient=" + patient + ", epoch=" + epoch + ", duration="
				+ duration + ", isConfirmed=" + isConfirmed + "]";
	}
	
}
