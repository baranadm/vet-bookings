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

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler { 

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorDTO body = new ErrorDTO(HttpStatus.BAD_REQUEST, ex.getFieldError().getDefaultMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(AnimalTypeAllreadyExistsException.class)
	ResponseEntity<Object> handleAnimalTypeAllreadyExists(AnimalTypeAllreadyExistsException ex, WebRequest request) {
		ErrorDTO body = new ErrorDTO(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(NumberFormatException.class)
	ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
		ErrorDTO body = new ErrorDTO(HttpStatus.BAD_REQUEST, "expected digit");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
}
