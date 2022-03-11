package pl.baranowski.dev.dto;

import java.util.List;

public class DoctorsFreeSlotsDTO {
	private final DoctorDTO doctorDTO;
	private final List<Long> availableEpochTimes;
	
	public DoctorsFreeSlotsDTO(DoctorDTO doctorDTO, List<Long> availableEpochTimes) {
		super();
		this.doctorDTO = doctorDTO;
		this.availableEpochTimes = availableEpochTimes;
	}

	public DoctorDTO getDoctorDTO() {
		return doctorDTO;
	}

	public List<Long> getAvailableEpochTimes() {
		return availableEpochTimes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((availableEpochTimes == null) ? 0 : availableEpochTimes.hashCode());
		result = prime * result + ((doctorDTO == null) ? 0 : doctorDTO.hashCode());
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
		DoctorsFreeSlotsDTO other = (DoctorsFreeSlotsDTO) obj;
		if (availableEpochTimes == null) {
			if (other.availableEpochTimes != null)
				return false;
		} else if (!availableEpochTimes.equals(other.availableEpochTimes))
			return false;
		if (doctorDTO == null) {
			if (other.doctorDTO != null)
				return false;
		} else if (!doctorDTO.equals(other.doctorDTO))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AvailableSlotsDTO [doctorDTO=" + doctorDTO + ", availableEpochTimes=" + availableEpochTimes + "]";
	}
	
}