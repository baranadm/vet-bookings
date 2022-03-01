package pl.baranowski.dev.builder;

import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.entity.Visit;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DoctorBuilder {
    private Long id;
    private String name;
    private String surname;
    private BigDecimal hourlyRate;
    private String nip;
    private Boolean active = true;
    private List<DayOfWeek> workingDays = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
    private Integer worksFromHour = 9;
    private Integer worksTillHour = 16;
    private Set<MedSpecialty> medSpecialties = new HashSet<>();
    private Set<AnimalType> animalTypes = new HashSet<>();
    private Set<Visit> visits = new HashSet<>();

    public DoctorBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DoctorBuilder surname(String surname) {
        this.surname = surname;
        return this;
    }

    public DoctorBuilder nip(String nip) {
        this.nip = nip;
        return this;
    }

    public DoctorBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public DoctorBuilder hourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
        return this;
    }

    public DoctorBuilder active(Boolean active) {
        this.active = active;
        return this;
    }

    public DoctorBuilder workingDays(List<DayOfWeek> workingDays) {
        this.workingDays = workingDays;
        return this;
    }

    public DoctorBuilder worksFromHour(Integer worksFromHour) {
        this.worksFromHour = worksFromHour;
        return this;
    }

    public DoctorBuilder worksTillHour(Integer worksTillHour) {
        this.worksTillHour = worksTillHour;
        return this;
    }

    public DoctorBuilder animalTypes(Set<AnimalType> animalTypes) {
        this.animalTypes = animalTypes;
        return this;
    }

    public DoctorBuilder medSpecialties(Set<MedSpecialty> medSpecialties) {
        this.medSpecialties = medSpecialties;
        return this;
    }

    public DoctorBuilder visits(Set<Visit> visits) {
        this.visits = visits;
        return this;
    }

    public Doctor build() {
        return new Doctor(id, name, surname, hourlyRate, nip, active, workingDays, worksFromHour, worksTillHour, animalTypes, medSpecialties, visits);
    }

}