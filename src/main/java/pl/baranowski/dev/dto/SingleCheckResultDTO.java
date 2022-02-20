package pl.baranowski.dev.dto;

import java.util.List;

public class SingleCheckResultDTO {
	private DoctorDTO doctorDTO;
	private List<Long> epochFreeTimes;
	
	public SingleCheckResultDTO(DoctorDTO doctorDTO, List<Long> epochFreeTimes) {
		super();
		this.doctorDTO = doctorDTO;
		this.epochFreeTimes = epochFreeTimes;
	}

	public DoctorDTO getDoctorDTO() {
		return doctorDTO;
	}

	public void setDoctorDTO(DoctorDTO doctorDTO) {
		this.doctorDTO = doctorDTO;
	}

	public List<Long> getEpochFreeTimes() {
		return epochFreeTimes;
	}

	public void setEpochFreeTimes(List<Long> epochFreeTimes) {
		this.epochFreeTimes = epochFreeTimes;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((epochFreeTimes == null) ? 0 : epochFreeTimes.hashCode());
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
		SingleCheckResultDTO other = (SingleCheckResultDTO) obj;
		if (epochFreeTimes == null) {
			if (other.epochFreeTimes != null)
				return false;
		} else if (!epochFreeTimes.equals(other.epochFreeTimes))
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
		return "CheckResultDTO [doctorDTO=" + doctorDTO + ", epochFreeTimes=" + epochFreeTimes + "]";
	}
	
}
