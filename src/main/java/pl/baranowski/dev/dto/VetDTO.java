package pl.baranowski.dev.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import pl.baranowski.dev.constraint.HourlyRateConstraint;
import pl.baranowski.dev.constraint.NipConstraint;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.MedSpecialty;

public class VetDTO {
	
	private Long id;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String surname;

	@HourlyRateConstraint
	private String hourlyRate;

	@NipConstraint
	private String nip;

	private Boolean active = true;

	private Set<MedSpecialty> medSpecialties = new HashSet<>();

	private Set<AnimalType> animalTypes = new HashSet<>();

	public VetDTO() {
	}

	public VetDTO(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.surname = builder.surname;
		this.hourlyRate = builder.hourlyRate;
		this.nip = builder.nip;
		this.active = builder.active;
		this.medSpecialties = builder.medSpecialties;
		this.animalTypes = builder.animalTypes;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getHourlyRate() {
		return hourlyRate;
	}

	public String getNip() {
		return nip;
	}

	public Boolean getActive() {
		return active;
	}

	public Set<MedSpecialty> getMedSpecialties() {
		return medSpecialties;
	}

	public Set<AnimalType> getAnimalTypes() {
		return animalTypes;
	}


	public static class Builder {
		private Long id; // optional
		private final String name; // required
		private final String surname; // required
		private String hourlyRate; // optional
		private String nip; // optional
		private Boolean active = true; // optional, default = true;
		private Set<MedSpecialty> medSpecialties = new HashSet<>(); // optional
		private Set<AnimalType> animalTypes = new HashSet<>(); // optional
		
		public Builder(String name, String surname) {
			this.name = name;
			this.surname = surname;
		}
		
		public Builder id(Long id) {
			this.id = id;
			return this;
		}
		
		public Builder hourlyRate(String hourlyRate) {
			this.hourlyRate = hourlyRate;
			return this;
		}
		
		public Builder nip(String nip) {
			this.nip = nip;
			return this;
		}
		
		public Builder active(Boolean active) {
			this.active = active;
			return this;
		}
		
		public Builder medSpecialties(Set<MedSpecialty> medSpecialties) {
			this.medSpecialties = medSpecialties;
			return this;
		}
		
		public Builder animalTypes(Set<AnimalType> animalTypes) {
			this.animalTypes = animalTypes;
			return this;
		}
		
		public VetDTO build() {
			return new VetDTO(this);
		}
		
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
