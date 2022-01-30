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
import pl.baranowski.dev.exception.AnimalTypeAllreadyExistsException;
import pl.baranowski.dev.exception.DoubledSpecialtyException;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.MedSpecialtyAllreadyExistsException;
import pl.baranowski.dev.exception.NIPExistsException;
import pl.baranowski.dev.exception.PatientAllreadyExistsException;
import pl.baranowski.dev.exception.VetNotActiveException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@SuppressWarnings("deprecation")
	@ExceptionHandler(value= {
		ConstraintViolationException.class,
		})
	protected ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(error.getHttpStatus()).contentType(MediaType.APPLICATION_JSON_UTF8).body(error);
	}
	@SuppressWarnings("deprecation")
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		MultiFieldsErrorDTO mfErrorDTO = new MultiFieldsErrorDTO();
		for (FieldError e : ex.getBindingResult().getFieldErrors()) {
			mfErrorDTO.addFieldError(new FieldValidationError(e.getField(), e.getDefaultMessage()));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON_UTF8).body(mfErrorDTO);

//		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
//		// works only for one invalid argument
//		error.setMessage(ex.getFieldError().getDefaultMessage());
//		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}
	

	@SuppressWarnings("deprecation")
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex.getClass().getSimpleName(), "Invalid or missing value for parameter [" + ex.getParameterName() + "]. Should be positive integer digits", HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(error.getHttpStatus()).contentType(MediaType.APPLICATION_JSON_UTF8).body(error);
	}

	@SuppressWarnings("deprecation")
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex.getClass().getSimpleName(), "Invalid or missing value for parameter [" + ex.getName() + "]. Should be positive integer digits", HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(error.getHttpStatus()).contentType(MediaType.APPLICATION_JSON_UTF8).body(error);
	}
	
	// i don't like it, can i delete it?
	@Override
//	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex.getCause().getClass().getSimpleName(), ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}

	@ExceptionHandler(AnimalTypeAllreadyExistsException.class)
	ResponseEntity<Object> handleAnimalTypeAllreadyExists(AnimalTypeAllreadyExistsException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}

	@ExceptionHandler(MedSpecialtyAllreadyExistsException.class)
	ResponseEntity<Object> handleMedSpecialtyAllreadyExists(MedSpecialtyAllreadyExistsException ex,
			WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}

	//TODO enhance invalid ID handling - ConstaintViolation? MethodArgumentNotValid?
	@SuppressWarnings("deprecation")
	@ExceptionHandler(NumberFormatException.class)
	ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
//		error.setMessage("digits expected");
		return ResponseEntity.status(error.getHttpStatus()).contentType(MediaType.APPLICATION_JSON_UTF8).body(error);
	}

	@SuppressWarnings("deprecation")
	@ExceptionHandler(EmptyFieldException.class)
	ResponseEntity<Object> handleEmptyFieldException(EmptyFieldException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		error.setMessage(ex.getMessage());
		return ResponseEntity.status(error.getHttpStatus()).contentType(MediaType.APPLICATION_JSON_UTF8).body(error);
	}

	@SuppressWarnings("deprecation")
	@ExceptionHandler(EntityNotFoundException.class)
	ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.NOT_FOUND);
		return ResponseEntity.status(error.getHttpStatus()).contentType(MediaType.APPLICATION_JSON_UTF8).body(error);
	}

	@ExceptionHandler(NIPExistsException.class)
	ResponseEntity<Object> handleNIPExistsException(NIPExistsException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}

	@ExceptionHandler(DoubledSpecialtyException.class)
	ResponseEntity<Object> handleDoubledSpecialtyException(DoubledSpecialtyException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.FORBIDDEN);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}

	@ExceptionHandler(VetNotActiveException.class)
	ResponseEntity<Object> handleVetNotActiveException(VetNotActiveException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.FORBIDDEN);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}

	@ExceptionHandler(PatientAllreadyExistsException.class)
	ResponseEntity<Object> handlePatientAllreadyExistsException(PatientAllreadyExistsException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.FORBIDDEN);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}
}
