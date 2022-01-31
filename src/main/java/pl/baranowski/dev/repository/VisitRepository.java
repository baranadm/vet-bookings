package pl.baranowski.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.baranowski.dev.entity.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long>{
	
	public List<Visit> findByEpochAndVetId(long epoch, long vetId);

}
