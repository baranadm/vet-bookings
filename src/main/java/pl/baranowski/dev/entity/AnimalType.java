package pl.baranowski.dev.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
	private Set<Patient> patientsAsOwners = new HashSet<>();
	
	@JsonIgnore
	@ManyToMany(mappedBy="animalTypes")
	private Set<Vet> matchingVets = new HashSet<>();

	public AnimalType() {
	}

	public AnimalType(String name) {
		this.name = name;
	}

	public AnimalType(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Patient> getPatientsAsOwners() {
		return patientsAsOwners;
	}
	
	public boolean enrollPatient(Patient patient) {
		return patientsAsOwners.add(patient);
	}
	
	public boolean delistPatient(Patient patient) {
		return patientsAsOwners.remove(patient);
	}
	
	public Set<Vet> getMatchingVets() {
		return matchingVets;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AnimalType other = (AnimalType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AnimalType [id=" + id + ", name=" + name + "]";
	}
	
}
