package pl.baranowski.dev.exception.doctor;

import org.springframework.http.HttpStatus;

public class DoctorAlreadyExistsException extends DoctorException {

    public DoctorAlreadyExistsException(String doctorNIP) {
        super(HttpStatus.FORBIDDEN, "Doctor with NIP: " + doctorNIP + " already exists in database.");
    }
}
