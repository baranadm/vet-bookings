package pl.baranowski.dev.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionMessageAndStatusAble {
    public String getMessage();
    public HttpStatus getHttpStatus();
}
