package pl.baranowski.dev.exception.animalType;

import org.springframework.http.HttpStatus;
import pl.baranowski.dev.exception.ApiException;

public class AnimalTypeException extends ApiException {

    public AnimalTypeException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public AnimalTypeException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
