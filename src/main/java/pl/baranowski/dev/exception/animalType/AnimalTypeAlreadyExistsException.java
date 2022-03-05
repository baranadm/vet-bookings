package pl.baranowski.dev.exception.animalType;

import org.springframework.http.HttpStatus;

public class AnimalTypeAlreadyExistsException extends AnimalTypeException {

    public AnimalTypeAlreadyExistsException(String animalTypeName) {
        super(HttpStatus.FORBIDDEN, "Animal type with name '" + animalTypeName + "' already exists in database.");
    }
}
