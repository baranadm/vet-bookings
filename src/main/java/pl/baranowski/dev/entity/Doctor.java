package pl.baranowski.dev.entity;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
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
public class Doctor {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private final String name;
	private final String surname;
	private BigDecimal hourlyRate;
	private final String nip;
	private Boolean active = true;
    @ElementCollection
    @CollectionTable(name="listOfWorkingDays")
	private List<DayOfWeek> workingDays = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
	private Integer worksFromHour = 9;
	private Integer worksTillHour = 16;
	@ManyToMany
	@JoinTable(
			name="doctors_to_med_specialities",
			joinColumns = @JoinColumn(name="doctor_id"),
			inverseJoinColumns = @JoinColumn(name="med_speciality_id")
	)
	private final Set<MedSpecialty> medSpecialties = new HashSet<>();
	@ManyToMany
	@JoinTable(
			name="doctors_to_animal_types",
			joinColumns = @JoinColumn(name="doctor_id"),
			inverseJoinColumns = @JoinColumn(name="animal_type_id")
	)
	private final Set<AnimalType> animalTypes = new HashSet<>();
	@JsonIgnore
	@OneToMany(mappedBy = "doctor")
	private final Set<Visit> visits = new HashSet<>();

	
	private Doctor(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.surname = builder.surname;
		this.hourlyRate = builder.hourlyRate.setScale(2);
		this.nip = builder.nip;
		this.active = builder.active;
		this.workingDays = builder.workingDays;
		this.worksFromHour = builder.worksFromHour;
		this.worksTillHour = builder.worksTillHour;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public BigDecimal getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(BigDecimal hourlyRate) {
		this.hourlyRate = hourlyRate.setScale(2);
	}

	public void setHourlyRateFromString(String hourlyRate) {
		this.hourlyRate = BigDecimal.valueOf(Double.parseDouble(hourlyRate)).setScale(2);
	}

	public String getNip() {
		return nip;
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
	
	public Boolean isActive() {
		return active;
	}
	
	public Set<AnimalType> getAnimalTypes() {
		return animalTypes;
	}
	
	public Set<MedSpecialty> getMedSpecialties() {
		return medSpecialties;
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
	
	public Integer getWorksFromHour() {
		return worksFromHour;
	}

	public void setWorksFromHour(Integer worksFromHour) {
		this.worksFromHour = worksFromHour;
	}

	public Integer getWorksTillHour() {
		return worksTillHour;
	}

	public void setWorksTillHour(Integer worksTillHour) {
		this.worksTillHour = worksTillHour;
	}

	public List<DayOfWeek> getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(List<DayOfWeek> workingDays) {
		this.workingDays = workingDays;
	}

	public boolean isAvailableAt(long epochInSeconds) {
		return worksAt(epochInSeconds) && !hasVisitsAtEpoch(epochInSeconds);
	}

	public boolean worksAt(long epochInSeconds) {
		ZonedDateTime zonedStart = toZonedDateTime(epochInSeconds);
		return worksAtTime(zonedStart) && worksAtDate(zonedStart);
	}
	
	private ZonedDateTime toZonedDateTime(long epochInSeconds) {
		Instant instant = Instant.ofEpochSecond(epochInSeconds);
		ZonedDateTime zoned = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		return zoned;
	}

	private boolean worksAtTime(ZonedDateTime zonedStart) {
		boolean afterStartOfWorkingHours = zonedStart.getHour() >= worksFromHour;
		boolean beforeEndOfWorkingHours = zonedStart.getHour() <= worksTillHour;
		return afterStartOfWorkingHours && beforeEndOfWorkingHours;
	}
	
	private boolean worksAtDate(ZonedDateTime zonedStart) {
		return workingDays.contains(zonedStart.getDayOfWeek());
	}

	
	public boolean hasVisitsAtEpoch(long epochInSeconds) {
		return visits.stream().anyMatch(visit -> 
								{
									boolean isEpochAfterVisitStart = epochInSeconds >= visit.getEpoch();
									boolean isEpochAfterVisitEnd = epochInSeconds < visit.getEpoch() + visit.getDuration();
									return isEpochAfterVisitStart && isEpochAfterVisitEnd;
								});
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
		Doctor other = (Doctor) obj;
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
		return "Doctor [id=" + id + ", name=" + name + ", surname=" + surname + ", hourlyRate=" + hourlyRate + ", nip="
				+ nip + ", active=" + active + "]";
	}
	
	public static class Builder {
		private Long id;
		private final String name;
		private final String surname;
		private BigDecimal hourlyRate;
		private final String nip;
		private Boolean active = true;
		private List<DayOfWeek> workingDays = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
		private Integer worksFromHour = 9;
		private Integer worksTillHour = 16;
		
		public Builder(String name, String surname, BigDecimal hourlyRate, String nip) {
			this.name = name;
			this.surname = surname;
			this.hourlyRate = hourlyRate;
			this.nip = nip;
		}
		
		public Builder id(Long id) {
			this.id = id;
			return this;
		}
		
		public Builder active(Boolean active) {
			this.active = active;
			return this;
		}
		
		public Builder workingDays(List<DayOfWeek> workingDays) {
			this.workingDays = workingDays;
			return this;
		}
		
		public Builder worksFromHour(Integer worksFromHour) {
			this.worksFromHour = worksFromHour;
			return this;
		}
		
		public Builder worksTillHour(Integer worksTillHour) {
			this.worksTillHour = worksTillHour;
			return this;
		}
		
		public Doctor build() {
			return new Doctor(this);
		}
		
	}
}
