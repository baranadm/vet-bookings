package pl.baranowski.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.baranowski.dev.entity.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long>{

}
