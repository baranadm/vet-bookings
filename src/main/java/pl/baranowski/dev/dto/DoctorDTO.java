package pl.baranowski.dev.dto;

import pl.baranowski.dev.builder.DoctorDTOBuilder;
import pl.baranowski.dev.constraint.HourlyRateConstraint;
import pl.baranowski.dev.constraint.NipConstraint;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public class DoctorDTO {

    private Long id;
    @NotBlank(message = "Name should not be empty.")
    private String name;
    @NotBlank(message = "Surname should not be empty.")
    private String surname;
    @HourlyRateConstraint(message = "Hourly rate should be positive number and should not be empty.")
    private String hourlyRate;
    @NipConstraint(message = "Invalid NIP. Should not be empty.")
    private String nip;
    private Boolean active; // default = true
    private Set<AnimalTypeDTO> animalTypes;
    private Set<MedSpecialtyDTO> medSpecialties;

    public DoctorDTO() {
    }

    public DoctorDTO(Long id,
                     String name,
                     String surname,
                     String hourlyRate,
                     String nip,
                     Boolean active,
                     Set<AnimalTypeDTO> animalTypes, Set<MedSpecialtyDTO> medSpecialties) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.hourlyRate = hourlyRate;
        this.nip = nip;
        //TODO pytanie: jak to działa po zamianie?
        this.active = active == null ? true : active;
        this.animalTypes = animalTypes;
        this.medSpecialties = medSpecialties;
    }

    public static DoctorDTOBuilder builder() {
        return new DoctorDTOBuilder();
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

    public Set<MedSpecialtyDTO> getMedSpecialties() {
        return medSpecialties;
    }

    public Set<AnimalTypeDTO> getAnimalTypes() {
        return animalTypes;
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
        DoctorDTO other = (DoctorDTO) obj;
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
        return "DoctorDTO [id=" + id + ", name=" + name + ", surname=" + surname + ", hourlyRate=" + hourlyRate + ", nip="
                + nip + ", active=" + active + "]";
    }

}
