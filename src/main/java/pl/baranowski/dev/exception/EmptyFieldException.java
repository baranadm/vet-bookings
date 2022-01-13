package pl.baranowski.dev.exception;

public class EmptyFieldException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4242892593084349448L;

	private String emptyFieldName;
	
	public EmptyFieldException(String fieldName) {
		this.emptyFieldName = fieldName;
	}

	@Override
	public String getMessage() {
		return "field [" + emptyFieldName + "] must not be empty";
	}
}
