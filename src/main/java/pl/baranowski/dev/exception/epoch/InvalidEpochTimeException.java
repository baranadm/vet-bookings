package pl.baranowski.dev.exception.epoch;

import org.springframework.http.HttpStatus;
import pl.baranowski.dev.exception.ApiException;

public class InvalidEpochTimeException extends ApiException {

    public InvalidEpochTimeException(String epochValue) {
        super(HttpStatus.BAD_REQUEST, "Invalid epoch: '" + epochValue + "'.");
    }
}
