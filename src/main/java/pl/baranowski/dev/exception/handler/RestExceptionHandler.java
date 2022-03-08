package pl.baranowski.dev.exception.handler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

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

import java.util.Iterator;
import java.util.Objects;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @SuppressWarnings("deprecation")
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        MultiFieldsErrorDTO mfErrorDTO = new MultiFieldsErrorDTO();
        for (FieldError e : exception.getBindingResult().getFieldErrors()) {
            mfErrorDTO.addFieldError(new FieldValidationError(e.getField(), e.getDefaultMessage()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(mfErrorDTO);
    }



    @SuppressWarnings("deprecation")
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        MultiFieldsErrorDTO mfErrorDTO = new MultiFieldsErrorDTO();
        exception.getConstraintViolations().forEach(violation -> {
            String invalidField = getInvalidFieldName(violation);
            mfErrorDTO.addFieldError(new FieldValidationError(getInvalidFieldName(violation),
                                                              violation.getMessage()));
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(mfErrorDTO);
    }

    private String getInvalidFieldName(ConstraintViolation<?> violation) {
        Path.Node node = null;
        for (Path.Node value : violation.getPropertyPath()) {
            // iterates to last node, which contains field name
            node = value;
        }
        return node.getName() == null ? "---" : node.getName();
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDTO error = new ErrorDTO(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).contentType(MediaType.APPLICATION_JSON_UTF8).body(error);
    }

    @SuppressWarnings("deprecation")
    @ExceptionHandler
    ResponseEntity<Object> handleApiException(ApiException exception, WebRequest request) {
        ErrorDTO error = new ErrorDTO(exception);
        return ResponseEntity.status(error.getHttpStatus()).contentType(MediaType.APPLICATION_JSON_UTF8).body(error);
    }
}
