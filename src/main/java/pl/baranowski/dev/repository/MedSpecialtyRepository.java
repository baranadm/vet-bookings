package pl.baranowski.dev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.baranowski.dev.entity.MedSpecialty;

@Repository
public interface MedSpecialtyRepository extends JpaRepository<MedSpecialty, Long> {
	Optional<MedSpecialty> findOneByName(String name);
	List<MedSpecialty> findByName(String name);

}
