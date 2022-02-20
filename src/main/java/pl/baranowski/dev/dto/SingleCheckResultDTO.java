package pl.baranowski.dev.dto;

import java.util.List;

public class SingleCheckResultDTO {
	private DoctorDTO vetDTO;
	private List<Long> epochFreeTimes;
	
	public SingleCheckResultDTO(DoctorDTO vetDTO, List<Long> epochFreeTimes) {
		super();
		this.vetDTO = vetDTO;
		this.epochFreeTimes = epochFreeTimes;
	}

	public DoctorDTO getVetDTO() {
		return vetDTO;
	}

	public void setVetDTO(DoctorDTO vetDTO) {
		this.vetDTO = vetDTO;
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
		result = prime * result + ((vetDTO == null) ? 0 : vetDTO.hashCode());
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
		if (vetDTO == null) {
			if (other.vetDTO != null)
				return false;
		} else if (!vetDTO.equals(other.vetDTO))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CheckResultDTO [vetDTO=" + vetDTO + ", epochFreeTimes=" + epochFreeTimes + "]";
	}
	
}
