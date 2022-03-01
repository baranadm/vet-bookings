package pl.baranowski.dev.builder;

import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.entity.Visit;

public class VisitBuilder {
    private Long id; // optional
    private Doctor doctor; // required
    private Patient patient; // required
    private Long epoch; // required
    private Long duration = 3600L; // optional, default = 3600
    private Boolean isConfirmed = false; // optional, default = false

    public VisitBuilder id(Long id) {
        this.id = id;
        return this;
    }
    public VisitBuilder doctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }

    public VisitBuilder patient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public VisitBuilder epoch(Long epoch) {
        this.epoch = epoch;
        return this;
    }

    public VisitBuilder duration(long duration) {
        this.duration = duration;
        return this;
    }

    public VisitBuilder isConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
        return this;
    }

    public Visit build() {
        return new Visit(id, doctor, patient, epoch, duration, isConfirmed);
    }
}
