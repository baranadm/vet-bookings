package pl.baranowski.dev.dto;

import org.springframework.http.HttpStatus;
import pl.baranowski.dev.exception.ApiException;

public class ErrorDTO {
    private final HttpStatus httpStatus
    private final String message;

    public ErrorDTO(ApiException exception) {
        this.httpStatus = exception.getHttpStatus();
        this.message = exception.getMessage();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
