package pl.baranowski.dev.exception;

public class AnimalTypeAllreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7581571989506921577L;

	public AnimalTypeAllreadyExistsException() {
		super("this animal type exists in database");
	}
	
}
