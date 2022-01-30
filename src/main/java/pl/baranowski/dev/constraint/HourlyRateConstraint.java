package pl.baranowski.dev.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import pl.baranowski.dev.validator.HourlyRateValidator;

@Documented
@Constraint(validatedBy=HourlyRateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HourlyRateConstraint {
	String message() default "Invalif field: \"hourlyRate\". Should be number greather than or equal to 0.0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
