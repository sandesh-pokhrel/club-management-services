package com.fitness.clientservice.exception;

import com.fitness.clientservice.common.Status;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final Status status;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        status.setExMessage(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Status> handleAlreadyExistsException(AlreadyExistsException ex) {
        status.setExMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Status> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        ex.printStackTrace();
        status.setExMessage("Database constriant violated. Please check the log.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Status> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        StringBuilder exceptionMessageBuilder = new StringBuilder(Strings.EMPTY);
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        exceptionMessageBuilder.append(fieldErrors.get(0).getDefaultMessage());
        //fieldErrors.forEach(fieldError -> exceptionMessageBuilder.append(fieldError.getDefaultMessage()));
        status.setExMessage(exceptionMessageBuilder.toString());
        return ResponseEntity.badRequest().body(status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAlreadyExistsException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().body("Generic exception caught");
    }
}
