package pl.baranowski.dev.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.entity.Visit;
import pl.baranowski.dev.repository.PatientRepository;
import pl.baranowski.dev.repository.VetRepository;
import pl.baranowski.dev.repository.VisitRepository;

@Service
public class VisitService {

	@Autowired
	VisitRepository visitRepository;
	@Autowired
	PatientRepository patientRepository;
	@Autowired
	VetRepository vetRepository;

	public List<Visit> findAll() {
		return visitRepository.findAll();
	}

	public Visit put(String vetId, String patientId, String dateTime) {
		return visitRepository.saveAndFlush(
				new Visit(
						vetRepository.findById(Long.decode(vetId)).get(), 
						patientRepository.findById(Long.decode(patientId)).get(), 
						OffsetDateTime.parse(dateTime)));
	}
	
}
