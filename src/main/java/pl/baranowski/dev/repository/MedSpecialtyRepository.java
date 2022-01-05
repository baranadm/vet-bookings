package pl.baranowski.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.baranowski.dev.entity.MedSpecialty;

public interface MedSpecialtyRepository extends JpaRepository<MedSpecialty, Long> {
	List<MedSpecialty> findByName(String name);

}
