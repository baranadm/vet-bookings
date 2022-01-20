package pl.baranowski.dev.exception;

public class DoubledSpecialtyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1262334028930586567L;
	
	private String specialty;
	private String value;
	
	public DoubledSpecialtyException(String specialty, String value) {
		this.specialty = specialty;
		this.value = value;
	}
	
	@Override
	public String getMessage() {
		return "doubled " + specialty + ": " + value;
	}
	
}
