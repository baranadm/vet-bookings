package pl.baranowski.dev.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NewPatientDTO {

	@NotBlank
	private String name;
	
	@Digits(fraction = 0, integer = 2)
	@Min(1)
	private int age;
	
	@NotBlank
	private String animalTypeName;
	
	@NotBlank
	private String ownerName;
	
	@NotNull
	@Email
	private String ownerEmail;

	public NewPatientDTO(@NotBlank String name, @NotNull int age, @NotBlank String animalTypeName,
			@NotBlank String ownerName, @NotBlank String ownerEmail) {
		this.name = name;
		this.age = age;
		this.animalTypeName = animalTypeName;
		this.ownerName = ownerName;
		this.ownerEmail = ownerEmail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAnimalTypeName() {
		return animalTypeName;
	}

	public void setAnimalTypeName(String animalTypeName) {
		this.animalTypeName = animalTypeName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + ((animalTypeName == null) ? 0 : animalTypeName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ownerEmail == null) ? 0 : ownerEmail.hashCode());
		result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
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
		NewPatientDTO other = (NewPatientDTO) obj;
		if (age != other.age)
			return false;
		if (animalTypeName == null) {
			if (other.animalTypeName != null)
				return false;
		} else if (!animalTypeName.equals(other.animalTypeName))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ownerEmail == null) {
			if (other.ownerEmail != null)
				return false;
		} else if (!ownerEmail.equals(other.ownerEmail))
			return false;
		if (ownerName == null) {
			if (other.ownerName != null)
				return false;
		} else if (!ownerName.equals(other.ownerName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NewPatientDTO [name=" + name + ", age=" + age + ", animalTypeName=" + animalTypeName + ", ownerName="
				+ ownerName + ", ownerEmail=" + ownerEmail + "]";
	}

}
