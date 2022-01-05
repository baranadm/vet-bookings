package pl.baranowski.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.baranowski.dev.entity.AnimalType;

public interface AnimalTypeRepository extends JpaRepository<AnimalType, Long> {

	List<AnimalType> findByName(String name);

}
