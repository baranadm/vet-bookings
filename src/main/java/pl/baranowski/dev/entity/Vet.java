package pl.baranowski.dev.entity;

import java.math.BigDecimal;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Vet {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	private String surname;
	
	private BigDecimal hourlyRate;
	
	private String nip;
	
	private Boolean active = true;
	
	@ManyToMany
	@JoinTable(
			name="vets_to_med_specialities",
			joinColumns = @JoinColumn(name="vet_id"),
			inverseJoinColumns = @JoinColumn(name="med_speciality_id")
	)
	private Set<MedSpecialty> medSpecialties = new HashSet<>();
	
	@ManyToMany
	@JoinTable(
			name="vets_to_animal_types",
			joinColumns = @JoinColumn(name="vet_id"),
			inverseJoinColumns = @JoinColumn(name="animal_type_id")
	)
	private Set<AnimalType> animalTypes = new HashSet<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "vet")
	private Set<Visit> visits = new HashSet<>();

	
	public Vet() {
	}
	
	/**
	 * 
	 * @param name
	 * @param surname
	 * @param hourlyRate - scale is automatically set to 2
	 * @param nip
	 */
	public Vet(String name, String surname, BigDecimal hourlyRate, String nip) {
		this.name = name;
		this.surname = surname;
		this.hourlyRate = hourlyRate.setScale(2);
		this.nip = nip;
	}

	/**
	 *  
	 * @param id
	 * @param name
	 * @param surname
	 * @param hourlyRate - scale is automatically set to 2
	 * @param nip
	 */
	public Vet(Long id, String name, String surname, BigDecimal hourlyRate, String nip) {
		this(name, surname, hourlyRate, nip);
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

	/**
	 * 
	 * @param hourlyRate - scale automatically set to 2
	 */
	public void setHourlyRate(BigDecimal hourlyRate) {
		this.hourlyRate = hourlyRate.setScale(2);
	}

	/**
	 * 
	 * @param hourlyRate - scale automatically set to 2
	 */
	public void setHourlyRateFromString(String hourlyRate) {
		this.hourlyRate = BigDecimal.valueOf(Double.parseDouble(hourlyRate)).setScale(2);
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
	
	public void setId(Long id) {
		this.id = id;
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

	public Set<AnimalType> getAnimalTypes() {
		return animalTypes;
	}
	
	public boolean addMedSpecialty(MedSpecialty ms) {
		return medSpecialties.add(ms);
	}
	
	public boolean addAnimalType(AnimalType at) {
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

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((hourlyRate == null) ? 0 : hourlyRate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Vet other = (Vet) obj;
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
		return "Vet [id=" + id + ", name=" + name + ", surname=" + surname + ", hourlyRate=" + hourlyRate + ", nip="
				+ nip + ", active=" + active + ", medSpecialties=" + medSpecialties + ", animalTypes=" + animalTypes
				+ ", visits=" + visits + "]";
	}
	
}
