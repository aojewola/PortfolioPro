package com.task.portfoliopro.api.v1;

import com.task.portfoliopro.errors.HttpError;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.AccessDeniedException;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@ControllerAdvice
public abstract class BaseController {
    @ExceptionHandler({HttpError.class})
    public ResponseEntity<?> handleError(final HttpError error) {
        log.error(error.getMessage());
        return ResponseEntity.status(error.getStatus()).body(error.getMessage());
    }

    // @ExceptionHandler({AccessDeniedException.class})
    // public ResponseEntity<?> handleError(final AccessDeniedException exception) {
    //     log.error("Exception occurred : ", exception);
    //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    // }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        String errorMessage = String.format(
                "Oops! Something went wrong: %s (status: %s)",
                ex.getReason(),
                ex.getStatusCode()
        );
        log.error(errorMessage);
        return ResponseEntity.status(ex.getStatusCode()).body(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        String errorMessage = "An unexpected error occurred. Please try again later. ";
        log.error(errorMessage + ex.getMessage());
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorMessage);
    }
} 