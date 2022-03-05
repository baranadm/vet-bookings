package pl.baranowski.dev.exception.medSpecialty;

import org.springframework.http.HttpStatus;

public class MedSpecialtyAlreadyExistsException extends MedSpecialtyException {

    public MedSpecialtyAlreadyExistsException(String medSpecialtyName) {
        super(HttpStatus.FORBIDDEN,
              "Medical specialty with name '" + medSpecialtyName + " already exists in database.");
    }

}
