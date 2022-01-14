package pl.baranowski.dev.exception;

public class DatabaseConnectionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -344636938503765727L;

	public DatabaseConnectionException() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getMessage() {
		return "Could not connect to database";
	}
}

