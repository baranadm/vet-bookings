package pl.baranowski.dev.dto;

import org.springframework.http.HttpStatus;
import pl.baranowski.dev.exception.ExceptionMessageAndStatusAble;

import java.util.Objects;

public class ErrorDTO {
	private String exceptionClassName;
	private HttpStatus httpStatus;
	private String message;

	public ErrorDTO() {
	}

	public ErrorDTO(ExceptionMessageAndStatusAble exception) {
		this.exceptionClassName = exception.getClass().getSimpleName();
		this.httpStatus = exception.getHttpStatus();
		this.message = exception.getMessage();
	}
	public ErrorDTO(Exception ex, HttpStatus httpStatus) {
		this.exceptionClassName = ex.getClass().getSimpleName();
		this.message = ex.getLocalizedMessage();
		this.httpStatus = httpStatus;
	}
	
	public ErrorDTO(String exceptionClassName, String message, HttpStatus httpStatus) {
		this.exceptionClassName = exceptionClassName;
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExceptionClassName() {
		return exceptionClassName;
	}

	public void setExceptionClassName(String exceptionClassName) {
		this.exceptionClassName = exceptionClassName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ErrorDTO errorDTO = (ErrorDTO) o;
		return Objects.equals(exceptionClassName,
							  errorDTO.exceptionClassName) && httpStatus == errorDTO.httpStatus && Objects.equals(
				message,
				errorDTO.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(exceptionClassName, httpStatus, message);
	}

	@Override
	public String toString() {
		return "ErrorDTO{" +
				"exceptionClassName='" + exceptionClassName + '\'' +
				", httpStatus=" + httpStatus +
				", message='" + message + '\'' +
				'}';
	}
}
