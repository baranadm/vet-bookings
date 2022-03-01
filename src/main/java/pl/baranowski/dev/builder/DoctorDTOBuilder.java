package pl.baranowski.dev.builder;

import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.MedSpecialty;

import java.util.HashSet;
import java.util.Set;

public class DoctorDTOBuilder {
    private Long id;
    private String name;
    private String surname;
    private String hourlyRate;
    private String nip;
    private Boolean active = true;
    private Set<MedSpecialty> medSpecialties = new HashSet<>();
    private Set<AnimalType> animalTypes = new HashSet<>();

    public DoctorDTOBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DoctorDTOBuilder surname(String surname) {
        this.surname = surname;
        return this;
    }

    public DoctorDTOBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public DoctorDTOBuilder hourlyRate(String hourlyRate) {
        this.hourlyRate = hourlyRate;
        return this;
    }

    public DoctorDTOBuilder nip(String nip) {
        this.nip = nip;
        return this;
    }

    public DoctorDTOBuilder active(Boolean active) {
        this.active = active;
        return this;
    }

    public DoctorDTOBuilder medSpecialties(Set<MedSpecialty> medSpecialties) {
        this.medSpecialties = medSpecialties;
        return this;
    }

    public DoctorDTOBuilder animalTypes(Set<AnimalType> animalTypes) {
        this.animalTypes = animalTypes;
        return this;
    }

    public DoctorDTO build() {
        return new DoctorDTO(id, name, surname, hourlyRate, nip, active, animalTypes, medSpecialties);
    }

}
