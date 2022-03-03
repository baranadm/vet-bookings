package pl.baranowski.dev.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends Exception implements ExceptionMessageAndStatusAble {
    private final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.HTTP_STATUS;
    }
}
