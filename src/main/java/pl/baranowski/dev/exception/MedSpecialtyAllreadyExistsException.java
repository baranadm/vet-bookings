package pl.baranowski.dev.exception;

public class MedSpecialtyAllreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7365859384153317904L;

	public MedSpecialtyAllreadyExistsException() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMessage() {
		return "this animal type exists in database";
	}
}
