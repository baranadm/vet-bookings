package pl.baranowski.dev.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class NewVisitDTO {
	
	@NotNull(message = "Please provide doctorId")
	@Pattern(regexp = "[0-9]+", message="Invalid doctorId - should contain digits only")
	private String doctorId;

	@NotNull(message = "Please provide patientId")
	@Pattern(regexp = "[0-9]+", message="Invalid patientId - should contain digits only")
	private String patientId;

	@NotNull(message = "Please provide epoch")
	@Pattern(regexp = "[0-9]+", message = "Invalid epoch format - only digits allowed")
	private String epoch;

	public NewVisitDTO() {
	}
	
	public NewVisitDTO(String vetId, String patientId, String epoch) {
		this.doctorId = vetId;
		this.patientId = patientId;
		this.epoch = epoch;
	}

	public String getVetId() {
		return doctorId;
	}


	public void setVetId(String vetId) {
		this.doctorId = vetId;
	}


	public String getPatientId() {
		return patientId;
	}


	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}


	public String getEpoch() {
		return epoch;
	}


	public void setEpoch(String epoch) {
		this.epoch = epoch;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((epoch == null) ? 0 : epoch.hashCode());
		result = prime * result + ((patientId == null) ? 0 : patientId.hashCode());
		result = prime * result + ((doctorId == null) ? 0 : doctorId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NewVisitDTO other = (NewVisitDTO) obj;
		if (epoch == null) {
			if (other.epoch != null)
				return false;
		} else if (!epoch.equals(other.epoch))
			return false;
		if (patientId == null) {
			if (other.patientId != null)
				return false;
		} else if (!patientId.equals(other.patientId))
			return false;
		if (doctorId == null) {
			if (other.doctorId != null)
				return false;
		} else if (!doctorId.equals(other.doctorId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NewVisitDTO [vetId=" + doctorId + ", patientId=" + patientId + ", epoch=" + epoch + "]";
	}
	
}
