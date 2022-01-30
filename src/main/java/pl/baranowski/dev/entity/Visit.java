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
	private Vet vet;
	
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;
	
	private long epoch;
	private Boolean isConfirmed;
	
	public Visit() {
	}

	public Visit(Long id, Vet vet, Patient patient, long epoch, Boolean confirmed) {
		this(id, vet, patient, epoch);
		this.isConfirmed = confirmed;
	}

	public Visit(long id, Vet vet, Patient patient, long epoch) {
		this.id = id;
		this.vet = vet;
		this.patient = patient;
		this.epoch = epoch;
		this.isConfirmed = false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((isConfirmed == null) ? 0 : isConfirmed.hashCode());
		result = prime * result + (int) (epoch ^ (epoch >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (isConfirmed == null) {
			if (other.isConfirmed != null)
				return false;
		} else if (!isConfirmed.equals(other.isConfirmed))
			return false;
		if (epoch != other.epoch)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		return "Visit [id=" + id + ", vet=" + vet + ", patient=" + patient + ", epoch=" + epoch + ", confirmed="
				+ isConfirmed + "]";
	}
	
}
