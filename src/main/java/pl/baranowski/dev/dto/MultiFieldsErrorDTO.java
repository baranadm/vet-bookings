package pl.baranowski.dev.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MultiFieldsErrorDTO that = (MultiFieldsErrorDTO) o;
		return Objects.equals(fieldErrors, that.fieldErrors);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldErrors);
	}

	@Override
	public String toString() {
		return "MultiFieldsErrorDTO{" +
				"fieldErrors=" + fieldErrors +
				'}';
	}
}
