package pl.baranowski.dev.model;

import java.util.List;

import pl.baranowski.dev.entity.Doctor;

public class DoctorsFreeSlots {
	private final Doctor doctor;
	private final List<Long> availableEpochTimes;
	
	public DoctorsFreeSlots(Doctor doctor, List<Long> epochFreeTimes) {
		this.doctor = doctor;
		this.availableEpochTimes = epochFreeTimes;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public List<Long> getEpochFreeTimes() {
		return availableEpochTimes;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((availableEpochTimes == null) ? 0 : availableEpochTimes.hashCode());
		result = prime * result + ((doctor == null) ? 0 : doctor.hashCode());
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
		DoctorsFreeSlots other = (DoctorsFreeSlots) obj;
		if (availableEpochTimes == null) {
			if (other.availableEpochTimes != null)
				return false;
		} else if (!availableEpochTimes.equals(other.availableEpochTimes))
			return false;
		if (doctor == null) {
			if (other.doctor != null)
				return false;
		} else if (!doctor.equals(other.doctor))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CheckResult [doctor=" + doctor + ", epochFreeTimes=" + availableEpochTimes + "]";
	}
	
}
