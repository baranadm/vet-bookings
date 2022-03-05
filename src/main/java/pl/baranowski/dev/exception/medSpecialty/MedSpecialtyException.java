package pl.baranowski.dev.exception.medSpecialty;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.exception.ApiException;

public class MedSpecialtyException extends ApiException {
    public MedSpecialtyException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public MedSpecialtyException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
