package pl.baranowski.dev.exception.handler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.dto.MultiFieldsErrorDTO;
import pl.baranowski.dev.error.FieldValidationError;
import pl.baranowski.dev.exception.*;
import pl.baranowski.dev.exception.animalType.AnimalTypeAlreadyExistsException;
import pl.baranowski.dev.exception.animalType.AnimalTypeException;
import pl.baranowski.dev.exception.doctor.DoctorDoubledSpecialtyException;
import pl.baranowski.dev.exception.doctor.DoctorNotActiveException;
import pl.baranowski.dev.exception.medSpecialty.MedSpecialtyAlreadyExistsException;
import pl.baranowski.dev.exception.patient.PatientAlreadyExistsException;
import pl.baranowski.dev.exception.search.SearchRequestInvalidException;
import pl.baranowski.dev.exception.visit.NewVisitNotPossibleException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@SuppressWarnings("deprecation")
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		MultiFieldsErrorDTO mfErrorDTO = new MultiFieldsErrorDTO();
		for (FieldError e : exception.getBindingResult().getFieldErrors()) {
			mfErrorDTO.addFieldError(new FieldValidationError(e.getField(), e.getDefaultMessage()));
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(mfErrorDTO);
	}

	@ExceptionHandler
	ResponseEntity<Object> handleApiException(ApiException exception, WebRequest request) {
		ErrorDTO error = new ErrorDTO(exception);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}
}
