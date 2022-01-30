package pl.baranowski.dev.exception;

public class PatientAllreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8968241444988384600L;

	private String message;

	public PatientAllreadyExistsException() {
	}
	
	public PatientAllreadyExistsException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message.isEmpty() ? super.getMessage() : message;
	}
}
