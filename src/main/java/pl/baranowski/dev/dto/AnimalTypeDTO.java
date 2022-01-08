package pl.baranowski.dev.dto;

import javax.validation.constraints.NotBlank;

public class AnimalTypeDTO {

	private Long id;
	@NotBlank(message = "name must not be null or empty")
	private String name;
	
	private AnimalTypeDTO() {
	}

	public AnimalTypeDTO(String name) {
		this.name = name;
	}
	
	public AnimalTypeDTO(Long id, @NotBlank String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}