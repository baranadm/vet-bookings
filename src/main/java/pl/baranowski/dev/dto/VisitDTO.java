package pl.baranowski.dev.dto;

public class VisitDTO {

	private long id;
	private DoctorDTO doctor;
	private PatientDTO patient;
	private long epoch;
	private Boolean isConfirmed;
	private Long duration;
	
	public VisitDTO() {
	}

	public VisitDTO(DoctorDTO doctor, PatientDTO patient, long epoch, Boolean isConfirmed) {
		this.doctor = doctor;
		this.patient = patient;
		this.epoch = epoch;
		this.isConfirmed = isConfirmed;
	}

	public VisitDTO(long id, DoctorDTO doctor, PatientDTO patient, long epoch, Boolean confirmed) {
		this.id = id;
		this.doctor = doctor;
		this.patient = patient;
		this.epoch = epoch;
		this.isConfirmed = confirmed;
	}

	public VisitDTO(long id, DoctorDTO doctor, PatientDTO patient, long epoch, Boolean isConfirmed, Long duration) {
		this.id = id;
		this.doctor = doctor;
		this.patient = patient;
		this.epoch = epoch;
		this.isConfirmed = isConfirmed;
		this.duration = duration;
	}

	public VisitDTO withId(long id) {
		return new VisitDTO(id, doctor, patient,epoch, isConfirmed);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DoctorDTO getDoctor() {
		return doctor;
	}

	public void setDoctor(DoctorDTO doctor) {
		this.doctor = doctor;
	}

	public PatientDTO getPatient() {
		return patient;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}

	public long getEpoch() {
		return epoch;
	}

	public void setEpoch(long epoch) {
		this.epoch = epoch;
	}

	public Boolean getConfirmed() {
		return isConfirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.isConfirmed = confirmed;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((isConfirmed == null) ? 0 : isConfirmed.hashCode());
		result = prime * result + (int) (epoch ^ (epoch >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
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
		VisitDTO other = (VisitDTO) obj;
		if (isConfirmed == null) {
			if (other.isConfirmed != null)
				return false;
		} else if (!isConfirmed.equals(other.isConfirmed))
			return false;
		if (epoch != other.epoch)
			return false;
		if (id != other.id)
			return false;
		if (patient == null) {
			if (other.patient != null)
				return false;
		} else if (!patient.equals(other.patient))
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
		return "VisitDTO [id=" + id + ", doctor=" + doctor + ", patient=" + patient + ", epoch=" + epoch + ", confirmed="
				+ isConfirmed + "]";
	}

}
