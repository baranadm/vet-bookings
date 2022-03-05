package pl.baranowski.dev.exception.doctor;

import org.springframework.http.HttpStatus;

public class DoctorDoubledSpecialtyException extends DoctorException {

    public DoctorDoubledSpecialtyException(String specialtyName) {
        super(HttpStatus.FORBIDDEN, "Doctor already has specialty '" + specialtyName + "'.");
    }
}
