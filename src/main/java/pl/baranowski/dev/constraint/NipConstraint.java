package pl.baranowski.dev.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import pl.baranowski.dev.validator.NipValidator;

@Documented
@Constraint(validatedBy=NipValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NipConstraint {
	String message() default "invalid NIP";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}