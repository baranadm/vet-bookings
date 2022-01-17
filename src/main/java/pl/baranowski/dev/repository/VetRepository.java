package pl.baranowski.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.baranowski.dev.entity.Vet;

public interface VetRepository extends JpaRepository<Vet, Long>{

	List<Vet> findByNip(String nip);

}
