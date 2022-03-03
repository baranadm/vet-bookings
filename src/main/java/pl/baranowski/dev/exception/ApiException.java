package pl.baranowski.dev.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends Exception implements ExceptionMessageAndStatusAble {
        private final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

        public ApiException(String message) {
                super(message);
        }

        @Override
        public HttpStatus getHttpStatus() {
                return this.HTTP_STATUS;
        }
}
