package pl.baranowski.dev.exception;

public class NIPExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4867600428016004198L;

	public NIPExistsException() {
	}
	
	@Override
	public String getMessage() {
		return "NIP already exists in database";
	}
	
}
