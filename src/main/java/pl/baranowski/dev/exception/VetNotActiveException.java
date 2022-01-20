package pl.baranowski.dev.exception;

public class VetNotActiveException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2067411146746067920L;

	private String customMessage;

	public VetNotActiveException() {
		customMessage = "";
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
	
	public VetNotActiveException withCustomMessage(String message) {
		this.setCustomMessage(message);
		return this;
	}

}
