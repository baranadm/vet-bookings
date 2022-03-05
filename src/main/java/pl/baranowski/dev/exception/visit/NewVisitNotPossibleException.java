package pl.baranowski.dev.exception.visit;

import org.springframework.http.HttpStatus;
import pl.baranowski.dev.dto.NewVisitDTO;
import pl.baranowski.dev.exception.ApiException;

public class NewVisitNotPossibleException extends ApiException {
    public NewVisitNotPossibleException(NewVisitDTO newVisitDTO) {
        this("Visit with params: " + newVisitDTO + " could not be created");
    }

	public NewVisitNotPossibleException(String message) {
		super(HttpStatus.FORBIDDEN, message);
	}

}
