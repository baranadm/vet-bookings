package pl.baranowski.dev.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Vet {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String name;
	private String surname;
	private Set<MedSpecialty> medSpecialty;
	private Set<AnimalType> animalType;
	private Double hourlyRate;
	private String nip;
	
	public Vet(String name, String surname, Double hourlyRate, String nip) {
		this.name = name;
		this.surname = surname;
		this.hourlyRate = hourlyRate;
		this.nip = nip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Double getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(Double hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public String getNip() {
		return nip;
	}

	public void setNip(String nip) {
		this.nip = nip;
	}

	public Long getId() {
		return id;
	}

	public Set<MedSpecialty> getMedSpecialty() {
		return medSpecialty;
	}

	public Set<AnimalType> getPetSpecialty() {
		return animalType;
	}
	
	public boolean addMedSpecialty(MedSpecialty ms) {
		return medSpecialty.add(ms);
	}
	
	public boolean addPetSpecialty(AnimalType at) {
		return animalType.add(at);
	}
	
	
	
}
