package pl.baranowski.dev.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import pl.baranowski.dev.constraint.HourlyRateConstraint;

public class HourlyRateValidator implements ConstraintValidator<HourlyRateConstraint, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// can not be null or empty
		if(value == null || value.isEmpty()) {
			return false;
		}
		// if value can't be parsed, return not valid
		Double parsed;
		try {
			parsed = Double.parseDouble(value);
		} catch (Exception e) {
			return false;
		}
		// parsed must be >=0
		if(parsed >= 0) {
			return true;
		}
		return false;
	}
}
