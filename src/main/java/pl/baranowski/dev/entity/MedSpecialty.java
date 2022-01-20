package pl.baranowski.dev.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class MedSpecialty {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;

	@JsonIgnore
	@ManyToMany(mappedBy = "medSpecialties")
	private Set<Vet> entitledVets = new HashSet<>();

	public MedSpecialty() {
	}

	public MedSpecialty(String name) {
		this.name = name;
	}
	
	public MedSpecialty(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
