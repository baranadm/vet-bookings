package pl.baranowski.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.entity.Doctor;

public interface VetRepository extends JpaRepository<Doctor, Long>{

	List<Doctor> findByNip(String nip);
	List<Doctor> findByAnimalTypesAndMedSpecialties(AnimalType animalType, MedSpecialty medSpecialty);

}
