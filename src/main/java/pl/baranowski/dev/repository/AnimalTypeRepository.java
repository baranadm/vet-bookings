package pl.baranowski.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.baranowski.dev.entity.AnimalType;

@Repository
public interface AnimalTypeRepository extends JpaRepository<AnimalType, Long> {

	List<AnimalType> findByName(String name);

}
