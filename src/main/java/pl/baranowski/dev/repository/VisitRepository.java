package pl.baranowski.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.baranowski.dev.entity.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long>{

	public List<Visit> findByDoctorId(long doctorId);
	public List<Visit> findByEpochInSecondsAndDoctorId(long epochInSeconds, long doctorId);
	public List<Visit> findByEpochInSecondsAndPatientId(long epochInSeconds, long patientId);

}
