package pl.baranowski.dev.entity;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Visit {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private Vet vet;
	private Patient patient;
	private OffsetDateTime dateTime;
	private Boolean confirmed;
	
	public Visit(Vet vet, Patient patient, OffsetDateTime dateTime) {
		this.vet = vet;
		this.patient = patient;
		this.dateTime = dateTime;
		this.confirmed = false;
	}

	public Vet getVet() {
		return vet;
	}

	public void setVet(Vet vet) {
		this.vet = vet;
	}

	public OffsetDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(OffsetDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Long getId() {
		return id;
	}

	public Patient getPatient() {
		return patient;
	}
	
	public Boolean isConfirmed() {
		return confirmed;
	}
	
	public void confirm() {
		confirmed = true;
	}
	
}
