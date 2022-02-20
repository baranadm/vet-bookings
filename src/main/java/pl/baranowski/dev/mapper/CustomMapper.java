package pl.baranowski.dev.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.entity.Doctor;
import pl.baranowski.dev.entity.Visit;

@Component
public class CustomMapper {

	public CustomMapper() {
	}
	
	// to ENTITY maps...
	
	public AnimalType toEntity(AnimalTypeDTO dto) {
		AnimalType at = new AnimalType(dto.getId(), dto.getName());
		return at;
	}
	
	public MedSpecialty toEntity(MedSpecialtyDTO dto) {
		MedSpecialty ms = new MedSpecialty(dto.getId(), dto.getName());
		return ms;
	}
	
	public Doctor toEntity(DoctorDTO dto) {
		Doctor doctor = new Doctor(dto.getId(), dto.getName(), dto.getSurname(), new BigDecimal(dto.getHourlyRate()), dto.getNip());
		doctor.setActive(dto.getActive());
		return doctor;
	}
	
	// to DTO maps...
	
	public AnimalTypeDTO toDto(AnimalType at) {
		AnimalTypeDTO dto = new AnimalTypeDTO(at.getId(), at.getName());
		return dto;
	}
	
	public MedSpecialtyDTO toDto(MedSpecialty ms) {
		MedSpecialtyDTO dto = new MedSpecialtyDTO(ms.getId(), ms.getName());
		return dto;
	}
	
	public PatientDTO toDto(Patient patient) {
		PatientDTO dto = new PatientDTO(patient.getId(), patient.getName(), toDto(patient.getAnimalType()), patient.getAge(), patient.getOwnerName(), patient.getOwnerEmail());
		return dto;
	}
	
	public DoctorDTO toDto(Doctor doctor) {
		DoctorDTO dto = new DoctorDTO.Builder(doctor.getName(), doctor.getSurname())
				.id(doctor.getId())
				.hourlyRate(doctor.getHourlyRate().toPlainString())
				.nip(doctor.getNip())
				.active(doctor.getActive())
				.medSpecialties(doctor.getMedSpecialties())
				.animalTypes(doctor.getAnimalTypes())
			.build();
		return dto;
	}
	
	public VisitDTO toDto(Visit visit) {
		VisitDTO dto = new VisitDTO(visit.getId(), toDto(visit.getDoctor()), toDto(visit.getPatient()), visit.getEpoch(), visit.getIsConfirmed());
		return dto;
	}
}
