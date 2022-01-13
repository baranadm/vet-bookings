package pl.baranowski.dev.exception.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.exception.AnimalTypeAllreadyExistsException;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.MedSpecialtyAllreadyExistsException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler { 

	// soon will have to handle few arguments not valid
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorDTO error = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		// works only for one invalid argument
		error.setMessage(ex.getFieldError().getDefaultMessage());
		return ResponseEntity.status(error.getHttpStatus()).body(error);
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
}
