package pl.baranowski.dev.exception;

import org.springframework.http.HttpStatus;

public class InvalidParamException extends ApiException {

    public InvalidParamException(String paramName, String value) {
        this("Invalid parameter [" + paramName + "], value='" + value + "'.");
    }

    public InvalidParamException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
