package pl.baranowski.dev.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import pl.baranowski.dev.constraint.NipConstraint;

public class NipValidator implements ConstraintValidator<NipConstraint, String>{

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value != null // cannot be null
				&& !value.isEmpty() //cannot be empty
				&& value.length() == 10 // must contain 10 characters
				&& value.matches("[0-9]+") // must contain only digits
				&& isValidNip(value);
	}
	
	private boolean isValidNip(String nip) {
		int[] weights = {6, 5, 7, 2, 3, 4, 5, 6, 7};
		int sum = 0;
		try {
			for(int i=0; i<weights.length; i++) {
				sum += Integer.parseInt(nip.substring(i, i+1)) * weights[i];
			}
			return (sum % 11) == Integer.parseInt(nip.substring(9, 10));
		} catch (NumberFormatException e) {
			// should never be here
			return false;
		}
		
	}

}
