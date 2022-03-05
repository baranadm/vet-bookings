package pl.baranowski.dev.dto;

import org.springframework.http.HttpStatus;
import pl.baranowski.dev.exception.ApiException;

import java.util.Objects;

public class ErrorDTO {
    private final HttpStatus httpStatus;
    private final String message;

    public ErrorDTO(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorDTO errorDTO = (ErrorDTO) o;
        return httpStatus == errorDTO.httpStatus && Objects.equals(message, errorDTO.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpStatus, message);
    }

    @Override
    public String toString() {
        return "ErrorDTO{" +
                "httpStatus=" + httpStatus +
                ", message='" + message + '\'' +
                '}';
    }
}
