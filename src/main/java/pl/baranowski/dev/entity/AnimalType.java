package pl.baranowski.dev.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class AnimalType {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;

	@Column(unique = true)
	private String name;

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

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AnimalType that = (AnimalType) o;
		return Objects.equals(id, that.id) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString() {
		return "AnimalType [id=" + id + ", name=" + name + "]";
	}
	
}
