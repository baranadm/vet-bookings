package pl.baranowski.dev.dto;

import java.util.Objects;

public class VisitDTO {

    private Long id;
    private DoctorDTO doctor;
    private PatientDTO patient;
    private Long epoch;
    private Boolean isConfirmed;
    private Long duration;

    public VisitDTO() {
    }

    public VisitDTO(DoctorDTO doctor, PatientDTO patient, Long epoch, Boolean isConfirmed) {
        this.doctor = doctor;
        this.patient = patient;
        this.epoch = epoch;
        this.isConfirmed = isConfirmed;
    }

    public VisitDTO(long id, DoctorDTO doctor, PatientDTO patient, Long epoch, Boolean confirmed) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.epoch = epoch;
        this.isConfirmed = confirmed;
    }

    public VisitDTO(long id, DoctorDTO doctor, PatientDTO patient, Long epoch, Boolean isConfirmed, Long duration) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.epoch = epoch;
        this.isConfirmed = isConfirmed;
        this.duration = duration;
    }

    public VisitDTO withId(Long id) {
        return new VisitDTO(id, doctor, patient, epoch, isConfirmed);
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public Long getEpoch() {
        return epoch;
    }

    public void setEpoch(Long epoch) {
        this.epoch = epoch;
    }

    public Boolean getConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.isConfirmed = confirmed;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitDTO visitDTO = (VisitDTO) o;
        return Objects.equals(id, visitDTO.id) && Objects.equals(doctor,
                                                                 visitDTO.doctor) && Objects.equals(
                patient,
                visitDTO.patient) && Objects.equals(epoch,
                                                    visitDTO.epoch) && Objects.equals(isConfirmed,
                                                                                      visitDTO.isConfirmed) && Objects.equals(
                duration,
                visitDTO.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, doctor, patient, epoch, isConfirmed, duration);
    }

    @Override
    public String toString() {
        return "VisitDTO [id=" + id + ", doctor=" + doctor + ", patient=" + patient + ", epoch=" + epoch + ", confirmed="
                + isConfirmed + "]";
    }

}
