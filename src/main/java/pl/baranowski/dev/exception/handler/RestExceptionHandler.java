package pl.baranowski.dev.exception.handler;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.dto.MultiFieldsErrorDTO;
import pl.baranowski.dev.error.FieldValidationError;
import pl.baranowski.dev.exception.AnimalTypeAllreadyExistsException;
import pl.baranowski.dev.exception.DoubledSpecialtyException;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.MedSpecialtyAllreadyExistsException;
import pl.baranowski.dev.exception.NIPExistsException;
import pl.baranowski.dev.exception.VetNotActiveException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler { 

	// soon will have to handle few arguments not valid
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		MultiFieldsErrorDTO mfErrorDTO = new MultiFieldsErrorDTO();
		
		for(FieldError e: ex.getBindingResult().getFieldErrors()) {
			mfErrorDTO.addFieldError(new FieldValidationError(e.getField(), e.getDefaultMessage()));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mfErrorDTO);
		
//		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
//		// works only for one invalid argument
//		error.setMessage(ex.getFieldError().getDefaultMessage());
//		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}

	@ExceptionHandler(AnimalTypeAllreadyExistsException.class)
	ResponseEntity<Object> handleAnimalTypeAllreadyExists(AnimalTypeAllreadyExistsException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}

	@ExceptionHandler(MedSpecialtyAllreadyExistsException.class)
	ResponseEntity<Object> handleMedSpecialtyAllreadyExists(MedSpecialtyAllreadyExistsException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}

	@ExceptionHandler(NumberFormatException.class)
	ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		error.setMessage("digits expected");
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}
	
	@ExceptionHandler(EmptyFieldException.class)
	ResponseEntity<Object> handleEmptyFieldException(EmptyFieldException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.NOT_FOUND);
		return ResponseEntity.status(error.getHttpStatus()).body(error);
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
}
