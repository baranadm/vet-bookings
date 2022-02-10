package pl.baranowski.dev.exception;

public class SearchRequestInvalidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6334242997033452119L;
	private String message;
	
	public SearchRequestInvalidException() {
		message = "Search failed: please check search criteria";
	}
	
	public SearchRequestInvalidException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
}
