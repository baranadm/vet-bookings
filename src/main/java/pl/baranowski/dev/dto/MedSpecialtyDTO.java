package pl.baranowski.dev.dto;

import javax.validation.constraints.NotBlank;

public class MedSpecialtyDTO {

	private Long id;
	
	@NotBlank(message = "specialty must not be null or empty")
	private String name;

	//	is it necessary?
	//	private Set<Vet> entitledVets = new HashSet<>();

	public MedSpecialtyDTO() {
	}

	public MedSpecialtyDTO(String name) {
		this.name = name;
	}

	public MedSpecialtyDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
