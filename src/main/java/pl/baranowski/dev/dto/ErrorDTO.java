package pl.baranowski.dev.dto;

import java.time.OffsetDateTime;
import java.util.Map;

public class ErrorDTO {
	private OffsetDateTime timestamp;
	private String message;
	private Map<String, String[]> parameters;
	
	public ErrorDTO(String message, Map<String, String[]> parameters) {
		this.timestamp = OffsetDateTime.now();
		this.message = message;
		this.parameters = parameters;
	}

	public OffsetDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(OffsetDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, String[]> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String[]> parameters) {
		this.parameters = parameters;
	}
	
}
