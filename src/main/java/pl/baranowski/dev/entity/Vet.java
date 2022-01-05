package pl.baranowski.dev.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Vet {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String name;
	private String surname;
	
	@ManyToMany
	@JoinTable(
			name="vets_to_med_specialities",
			joinColumns = @JoinColumn(name="vet_id"),
			inverseJoinColumns = @JoinColumn(name="med_speciality_id")
	)
	private Set<MedSpecialty> medSpecialties;
	
	@ManyToMany
	@JoinTable(
			name="vets_to_animal_types",
			joinColumns = @JoinColumn(name="vet_id"),
			inverseJoinColumns = @JoinColumn(name="animal_type_id")
	)
	private Set<AnimalType> animalTypes;
	
	@OneToMany(mappedBy = "vet")
	private Set<Visit> visits = new HashSet<>();

	private Double hourlyRate;
	private String nip;
	
	public Vet() {
	}
	
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

	public Set<MedSpecialty> getMedSpecialties() {
		return medSpecialties;
	}

	public Set<AnimalType> getPetSpecialties() {
		return animalTypes;
	}
	
	public boolean addMedSpecialty(MedSpecialty ms) {
		return medSpecialties.add(ms);
	}
	
	public boolean addPetSpecialty(AnimalType at) {
		return animalTypes.add(at);
	}

	public Set<Visit> getVisits() {
		return visits;
	}
	
	public boolean addVisit(Visit visit) {
		return visits.add(visit);
	}
	
	public boolean removeVisit(Visit visit) {
		return visits.remove(visit);
	}
	
	
}
