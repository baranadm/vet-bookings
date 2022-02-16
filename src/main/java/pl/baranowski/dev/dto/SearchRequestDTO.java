package pl.baranowski.dev.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

// TODO delete class, not used
public class SearchRequestDTO {
	
	@NotBlank(message="Invalid search criteria: animalTypeName should not be empty.")
	private String animalTypeName;
	
	@NotBlank(message="Invalid search criteria: medSpecialtyName should not be empty.")
	private String medSpecialtyName;
	
	@NotBlank(message="Invalid search criteria: epochStart should not be empty.")
	@Pattern(regexp = "[0-9]+", message = "Invalid epoch format - only digits allowed")
	private String epochStart;
	
	@NotBlank(message="Invalid search criteria: epochEnd should not be empty.")
	@Pattern(regexp = "[0-9]+", message = "Invalid epoch format - only digits allowed")
	private String epochEnd;
	
	@NotBlank(message="Invalid search criteria: interval should not be empty.")
	@Pattern(regexp = "[0-9]+", message = "Invalid interval format - only digits allowed")
	private String interval;
	
	public SearchRequestDTO(String animalTypeName, String medSpecialtyName, String epochStart, String epochEnd, String interval) {
		this.animalTypeName = animalTypeName;
		this.medSpecialtyName = medSpecialtyName;
		this.epochStart = epochStart;
		this.epochEnd = epochEnd;
		this.interval = interval;
	}

	public String getAnimalTypeName() {
		return animalTypeName;
	}

	public void setAnimalTypeName(String animalTypeName) {
		this.animalTypeName = animalTypeName;
	}

	public String getMedSpecialtyName() {
		return medSpecialtyName;
	}

	public void setMedSpecialtyName(String medSpecialtyName) {
		this.medSpecialtyName = medSpecialtyName;
	}

	public String getEpochStart() {
		return epochStart;
	}

	public void setEpochStart(String epochStart) {
		this.epochStart = epochStart;
	}

	public String getEpochEnd() {
		return epochEnd;
	}

	public void setEpochEnd(String epochEnd) {
		this.epochEnd = epochEnd;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((animalTypeName == null) ? 0 : animalTypeName.hashCode());
		result = prime * result + ((epochEnd == null) ? 0 : epochEnd.hashCode());
		result = prime * result + ((epochStart == null) ? 0 : epochStart.hashCode());
		result = prime * result + ((interval == null) ? 0 : interval.hashCode());
		result = prime * result + ((medSpecialtyName == null) ? 0 : medSpecialtyName.hashCode());
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
		SearchRequestDTO other = (SearchRequestDTO) obj;
		if (animalTypeName == null) {
			if (other.animalTypeName != null)
				return false;
		} else if (!animalTypeName.equals(other.animalTypeName))
			return false;
		if (epochEnd == null) {
			if (other.epochEnd != null)
				return false;
		} else if (!epochEnd.equals(other.epochEnd))
			return false;
		if (epochStart == null) {
			if (other.epochStart != null)
				return false;
		} else if (!epochStart.equals(other.epochStart))
			return false;
		if (interval == null) {
			if (other.interval != null)
				return false;
		} else if (!interval.equals(other.interval))
			return false;
		if (medSpecialtyName == null) {
			if (other.medSpecialtyName != null)
				return false;
		} else if (!medSpecialtyName.equals(other.medSpecialtyName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SearchRequestDTO [animalTypeName=" + animalTypeName + ", medSpecialtyName=" + medSpecialtyName
				+ ", epochStart=" + epochStart + ", epochEnd=" + epochEnd + ", interval=" + interval + "]";
	}

}
