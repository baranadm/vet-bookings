package pl.baranowski.dev.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.entity.Patient;
import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.entity.Visit;

@Component
public class CustomMapper {

	public CustomMapper() {
	}
	
	// to ENTITY maps...
	
	public Vet toEntity(VetDTO dto) {
		Vet vet = new Vet(dto.getId(), dto.getName(), dto.getSurname(), new BigDecimal(dto.getHourlyRate()), dto.getNip());
		vet.setActive(dto.getActive());
		return vet;
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
	
	public VetDTO toDto(Vet vet) {
		VetDTO dto = new VetDTO.Builder(vet.getName(), vet.getSurname())
				.id(vet.getId())
				.hourlyRate(vet.getHourlyRate().toPlainString())
				.nip(vet.getNip())
				.active(vet.getActive())
				.medSpecialties(vet.getMedSpecialties())
				.animalTypes(vet.getAnimalTypes())
			.build();
		return dto;
	}
	
	public VisitDTO toDto(Visit visit) {
		VisitDTO dto = new VisitDTO(visit.getId(), toDto(visit.getVet()), toDto(visit.getPatient()), visit.getEpoch(), visit.getIsConfirmed());
		return dto;
	}
}
