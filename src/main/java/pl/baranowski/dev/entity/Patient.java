package pl.baranowski.dev.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Patient {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String name;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "animal_type_id", referencedColumnName = "id")
	private AnimalType animalType;
	private int age;
	private String ownerName;
	private String ownerEmail;
	
	public Patient() {
	}
	
	public Patient(String name, AnimalType animalType, int age, String ownerName, String ownerEmail) {
		this.name = name;
		this.animalType = animalType;
		this.age = age;
		this.ownerName = ownerName;
		this.ownerEmail = ownerEmail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
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

	public Long getId() {
		return id;
	}

	public AnimalType getAnimalType() {
		return animalType;
	}
	
}
