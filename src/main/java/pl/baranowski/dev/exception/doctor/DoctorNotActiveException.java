package pl.baranowski.dev.exception.doctor;

import org.springframework.http.HttpStatus;

public class DoctorNotActiveException extends DoctorException {

    public DoctorNotActiveException(Long doctorId) {
        super(HttpStatus.FORBIDDEN, "Doctor with id=" + doctorId + " is not active.");
    }
}
