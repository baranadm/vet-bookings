package pl.baranowski.dev.exception;

public class NewVisitNotPossibleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1887376175082552733L;

	private String message;
	
	public NewVisitNotPossibleException() {
		message = "Visit could not be created. Please verify input.";
	}

	public NewVisitNotPossibleException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
