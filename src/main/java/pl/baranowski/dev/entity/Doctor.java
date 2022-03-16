package pl.baranowski.dev.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.baranowski.dev.builder.DoctorBuilder;

@Entity
public class Doctor {
    private final static Logger LOGGER = LoggerFactory.getLogger(Doctor.class);
    private final static List<DayOfWeek> DEFAULT_WORKING_DAYS = Arrays.asList(DayOfWeek.MONDAY,
                                                                              DayOfWeek.TUESDAY,
                                                                              DayOfWeek.WEDNESDAY,
                                                                              DayOfWeek.THURSDAY,
                                                                              DayOfWeek.FRIDAY);
    private final static Integer DEFAULT_WORKS_FROM_HOUR = 9;
    private final static Integer DEFAULT_WORKS_TILL_HOUR = 16;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String surname;
    private BigDecimal hourlyRate;
    private String nip;
    private Boolean active = true;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "listOfWorkingDays")
    private List<DayOfWeek> workingDays;
    private Integer worksFromHour;
    private Integer worksTillHour;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "doctors_to_animal_types",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_type_id")
    )
    private Set<AnimalType> animalTypes = new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "doctors_to_med_specialities",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "med_speciality_id")
    )
    private Set<MedSpecialty> medSpecialties = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "doctor")
    private Set<Visit> visits = new HashSet<>();

    public Doctor() {
    }

    public Doctor(Long id,
                  String name,
                  String surname,
                  BigDecimal hourlyRate,
                  String nip,
                  Boolean active,
                  List<DayOfWeek> workingDays,
                  Integer worksFromHour,
                  Integer worksTillHour,
                  Set<AnimalType> animalTypes,
                  Set<MedSpecialty> medSpecialties,
                  Set<Visit> visits) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.hourlyRate = hourlyRate == null ? null : hourlyRate.setScale(2, RoundingMode.UP);
        this.nip = nip;
        this.active = active == null ? true : active;
        this.workingDays = (workingDays != null) ? workingDays : DEFAULT_WORKING_DAYS;
        this.worksFromHour = (worksFromHour != null) ? worksFromHour : DEFAULT_WORKS_FROM_HOUR;
        this.worksTillHour = (worksTillHour != null) ? worksTillHour : DEFAULT_WORKS_TILL_HOUR;
        this.animalTypes = animalTypes;
        this.medSpecialties = medSpecialties;
        this.visits = visits;
        LOGGER.debug("New Doctor has been created: {}", this);
    }

    public static DoctorBuilder builder() {
        return new DoctorBuilder();
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
        this.hourlyRate = hourlyRate.setScale(2, RoundingMode.UP);
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

    public Boolean isActive() {
        return active;
    }

    public List<DayOfWeek> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(List<DayOfWeek> workingDays) {
        this.workingDays = workingDays;
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

    public Set<AnimalType> getAnimalTypes() {
        return animalTypes;
    }

    public void setAnimalTypes(Set<AnimalType> animalTypes) {
        this.animalTypes = animalTypes;
    }

    public Set<MedSpecialty> getMedSpecialties() {
        return medSpecialties;
    }

    public void setMedSpecialties(Set<MedSpecialty> medSpecialties) {
        this.medSpecialties = medSpecialties;
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

    public void setVisits(Set<Visit> visits) {
        this.visits = visits;
    }

    public boolean addVisit(Visit visit) {
        return visits.add(visit);
    }

    public boolean removeVisit(Visit visit) {
        return visits.remove(visit);
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
        boolean beforeEndOfWorkingHours = zonedStart.getHour() < worksTillHour;
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

}
