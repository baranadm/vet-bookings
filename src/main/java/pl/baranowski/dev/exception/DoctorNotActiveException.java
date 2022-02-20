package pl.baranowski.dev.exception;

public class DoctorNotActiveException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2067411146746067920L;

	private String customMessage;

	public DoctorNotActiveException() {
		customMessage = "";
	}
	
	public DoctorNotActiveException(String customMessage) {
		this.customMessage = customMessage;
	}

	@Override
	public String getLocalizedMessage() {
		if(!customMessage.isEmpty()) {
			return customMessage;
		}
		return super.getLocalizedMessage();
	}

	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}
	
	public DoctorNotActiveException withCustomMessage(String message) {
		this.setCustomMessage(message);
		return this;
	}

}
