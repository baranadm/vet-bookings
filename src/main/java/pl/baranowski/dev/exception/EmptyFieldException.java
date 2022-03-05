package pl.baranowski.dev.exception;

import org.springframework.http.HttpStatus;

public class EmptyFieldException extends ApiException {
    public EmptyFieldException(String fieldName) {
        super(HttpStatus.BAD_REQUEST, "Field [" + fieldName + "] should not be empty.");
    }

}
