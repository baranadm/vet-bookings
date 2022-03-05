package pl.baranowski.dev.exception.search;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.baranowski.dev.exception.ApiException;

@ResponseStatus()
public class SearchRequestInvalidException extends ApiException {

    public SearchRequestInvalidException() {
        this("Search failed: please check search criteria.");
    }

    public SearchRequestInvalidException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
