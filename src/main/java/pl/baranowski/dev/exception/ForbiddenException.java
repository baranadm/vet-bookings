package pl.baranowski.dev.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends Exception implements ExceptionMessageAndStatusAble {
    private final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.HTTP_STATUS;
    }
}
