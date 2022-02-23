package pl.baranowski.dev.exception;

public class InvalidEpochTimeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3605981663483617486L;
	private String message;
	
	public InvalidEpochTimeException() {
	}

	public InvalidEpochTimeException(String message) {
		super();
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message.isEmpty() ? "Epoch time is invalid." : message;
	}
	
}
