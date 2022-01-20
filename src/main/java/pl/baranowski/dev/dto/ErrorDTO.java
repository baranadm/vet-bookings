package pl.baranowski.dev.dto;

import org.springframework.http.HttpStatus;

public class ErrorDTO {
	private String exceptionClassName;
	private HttpStatus httpStatus;
	private String message;

	public ErrorDTO() {
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
	
}
