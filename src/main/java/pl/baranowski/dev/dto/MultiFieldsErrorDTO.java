package pl.baranowski.dev.dto;

import java.util.ArrayList;
import java.util.List;

import pl.baranowski.dev.error.FieldValidationError;

public class MultiFieldsErrorDTO {

	private List<FieldValidationError> fieldErrors = new ArrayList<>();

	public MultiFieldsErrorDTO() {
	}
	
	public MultiFieldsErrorDTO(FieldValidationError error) {
		this.fieldErrors.add(error);
	}

	public List<FieldValidationError> getFieldErrors() {
		return fieldErrors;
	}
	
	public boolean addFieldError(FieldValidationError error) {
		return fieldErrors.add(error);
	}
	
}
