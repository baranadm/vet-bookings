package pl.baranowski.dev.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Patient {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "animal_type_id")
	private AnimalType animalType;

	@JsonIgnore
	@OneToMany(mappedBy="patient")
	private Set<Visit> visits = new HashSet<>();
	
	private Integer age;
	private String ownerName;
	private String ownerEmail;
	
	public Patient() {
	}
	
	public Patient(String name, AnimalType animalType, Integer age, String ownerName, String ownerEmail) {
		this.name = name;
		this.animalType = animalType;
		this.age = age;
		this.ownerName = ownerName;
		this.ownerEmail = ownerEmail;
	}
	
	public Patient(Long id, String name, AnimalType animalType, Integer age, String ownerName, String ownerEmail) {
		this.id = id;
		this.name = name;
		this.animalType = animalType;
		this.age = age;
		this.ownerName = ownerName;
		this.ownerEmail = ownerEmail;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Patient withId(Long id) {
		return new Patient(id, this.name, this.animalType, this.age, this.ownerName, this.ownerEmail);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AnimalType getAnimalType() {
		return animalType;
	}

	public void setAnimalType(AnimalType animalType) {
		this.animalType = animalType;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	
	public Set<Visit> getVisits() {
		return visits;
	}
	
	public boolean addVisit(Visit visit) {
		return visits.add(visit);
	}

	public boolean hasVisitsAt(long epochInSeconds) {
		return visits.stream().filter(visit -> visit.getEpoch() == epochInSeconds).count() > 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + ((animalType == null) ? 0 : animalType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ownerEmail == null) ? 0 : ownerEmail.hashCode());
		result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
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
		Patient other = (Patient) obj;
		if (age != other.age)
			return false;
		if (animalType == null) {
			if (other.animalType != null)
				return false;
		} else if (!animalType.equals(other.animalType))
			return false;
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
		if (ownerEmail == null) {
			if (other.ownerEmail != null)
				return false;
		} else if (!ownerEmail.equals(other.ownerEmail))
			return false;
		if (ownerName == null) {
			if (other.ownerName != null)
				return false;
		} else if (!ownerName.equals(other.ownerName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Patient [id=" + id + ", name=" + name + ", animalType=" + animalType + ", age=" + age + ", ownerName="
				+ ownerName + ", ownerEmail=" + ownerEmail + "]";
	}

}
