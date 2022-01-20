package pl.baranowski.dev.dto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import pl.baranowski.dev.constraint.NipConstraint;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.MedSpecialty;

public class VetDTO {
	
	private Long id;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String surname;
	
	@NotNull
	@DecimalMin(value="0.0", inclusive=true)
	@Digits(integer=4, fraction=2)
	private BigDecimal hourlyRate;
	
	@NipConstraint
	private String nip;
	
	private Boolean active = true;
	
	private Set<MedSpecialty> medSpecialties = new HashSet<>();
	private Set<AnimalType> animalTypes = new HashSet<>();

	public VetDTO() {
		// TODO Auto-generated constructor stub
	}
	public VetDTO(String name, String surname, BigDecimal hourlyRate, String nip) {
		this.name = name;
		this.surname = surname;
		this.hourlyRate = hourlyRate;
		this.nip = nip;
	}
	
	public VetDTO(Long id, String name, String surname, BigDecimal hourlyRate, String nip) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.hourlyRate = hourlyRate;
		this.nip = nip;
	}
	
	public VetDTO(Long id, String name, String surname, BigDecimal hourlyRate, String nip, Boolean active) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.hourlyRate = hourlyRate;
		this.nip = nip;
		this.active = active;
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

	public BigDecimal getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(BigDecimal hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public String getNip() {
		return nip;
	}

	public void setNip(String nip) {
		this.nip = nip;
	}

	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((hourlyRate == null) ? 0 : hourlyRate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nip == null) ? 0 : nip.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
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
		VetDTO other = (VetDTO) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (hourlyRate == null) {
			if (other.hourlyRate != null)
				return false;
		} else if (!hourlyRate.equals(other.hourlyRate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nip == null) {
			if (other.nip != null)
				return false;
		} else if (!nip.equals(other.nip))
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "VetDTO [id=" + id + ", name=" + name + ", surname=" + surname + ", hourlyRate=" + hourlyRate + ", nip="
				+ nip + ", active=" + active + "]";
	}
	
}
