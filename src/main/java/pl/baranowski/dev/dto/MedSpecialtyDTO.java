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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MedSpecialtyDTO other = (MedSpecialtyDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}