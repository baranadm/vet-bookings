package pl.baranowski.dev.error;

import java.util.Objects;

public class FieldValidationError {
	
	private final String field;
	private final String message;
	
	public FieldValidationError(String field, String message) {
		this.field = field;
		this.message = message;
	}

	public String getField() {
		return field;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FieldValidationError that = (FieldValidationError) o;
		return Objects.equals(field, that.field) && Objects.equals(message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, message);
	}

	@Override
	public String toString() {
		return "FieldValidationError{" +
				"field='" + field + '\'' +
				", message='" + message + '\'' +
				'}';
	}
}
