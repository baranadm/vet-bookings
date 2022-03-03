package pl.baranowski.dev.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends Exception implements ExceptionMessageAndStatusAble {
    private final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.HTTP_STATUS;
    }
}
