package pl.baranowski.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.baranowski.dev.entity.AnimalType;

public interface AnimalTypeRepository extends JpaRepository<AnimalType, Long> {

}
