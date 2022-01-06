package pl.baranowski.dev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.baranowski.dev.entity.Vet;
import pl.baranowski.dev.repository.VetRepository;

@Service
public class VetService {

	@Autowired
	private final VetRepository vetRepository;

	public VetService(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	public Vet put(Vet vet) {
		return vetRepository.saveAndFlush(vet);
	}
	
}
