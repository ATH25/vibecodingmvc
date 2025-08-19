package tom.springframework.vibecodingmvc.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    ResponseEntity<ProblemDetail> handleValidation(Exception ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("about:blank#validation-error"));
        pd.setProperty("errors", extractErrors(ex));
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    ResponseEntity<ProblemDetail> handleNotFound(Exception ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource not found");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("about:blank#not-found"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler({OptimisticLockingFailureException.class})
    ResponseEntity<ProblemDetail> handleConflict(Exception ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Optimistic lock conflict");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("about:blank#optimistic-lock"));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    private Map<String, Object> extractErrors(Exception ex) {
        Map<String, Object> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException manve) {
            manve.getBindingResult().getFieldErrors()
                    .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        } else if (ex instanceof ConstraintViolationException cve) {
            cve.getConstraintViolations()
                    .forEach(cv -> errors.put(cv.getPropertyPath().toString(), cv.getMessage()));
        }
        return errors;
    }
}
