package pl.baranowski.dev.exception.doctor;

import org.springframework.http.HttpStatus;
import pl.baranowski.dev.exception.ApiException;

public class DoctorException extends ApiException {
    public DoctorException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    public DoctorException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
