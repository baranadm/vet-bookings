package pl.baranowski.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.baranowski.dev.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}