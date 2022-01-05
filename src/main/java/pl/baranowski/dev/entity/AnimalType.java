package pl.baranowski.dev.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class AnimalType {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	@JsonIgnore
	@OneToMany(mappedBy = "animalType")
	private Set<Patient> patientsAsOwners = new HashSet();

	public AnimalType() {
	}

	public AnimalType(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public Set<Patient> getPatientsAsOwners() {
		return patientsAsOwners;
	}
	
	// will it update the correct entity, or create a new one?
	public boolean enrollPatient(Patient patient) {
		return patientsAsOwners.add(patient);
	}
	
}
