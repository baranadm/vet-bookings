package pl.baranowski.dev.exception.patient;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.baranowski.dev.dto.NewPatientDTO;
import pl.baranowski.dev.exception.ApiException;

public class PatientAlreadyExistsException extends ApiException {

    public PatientAlreadyExistsException(NewPatientDTO patientDTO) {
        this("Patient with params: " + patientDTO + " already exists in database.");
    }

    public PatientAlreadyExistsException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
