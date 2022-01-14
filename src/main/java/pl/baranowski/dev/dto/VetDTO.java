package pl.baranowski.dev.dto;

import java.util.HashSet;
import java.util.Set;

import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.MedSpecialty;

public class VetDTO {

	private Long id;
	private String name;
	private String surname;
	private Double hourlyRate;
	private String nip;
	private Set<MedSpecialty> medSpecialties = new HashSet<>();
	private Set<AnimalType> animalTypes = new HashSet<>();
	
	public VetDTO(Long id, String name, String surname, Double hourlyRate, String nip) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.hourlyRate = hourlyRate;
		this.nip = nip;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Set<MedSpecialty> getMedSpecialties() {
		return medSpecialties;
	}

	public void setMedSpecialties(Set<MedSpecialty> medSpecialties) {
		this.medSpecialties = medSpecialties;
	}

	public Set<AnimalType> getAnimalTypes() {
		return animalTypes;
	}

	public void setAnimalTypes(Set<AnimalType> animalTypes) {
		this.animalTypes = animalTypes;
	}

	@Override
	public String toString() {
		return "VetDTO [id=" + id + ", name=" + name + ", surname=" + surname + ", hourlyRate=" + hourlyRate + ", nip="
				+ nip + "]";
	}
	
}
