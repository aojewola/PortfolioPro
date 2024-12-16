package com.task.portfoliopro.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
@AllArgsConstructor
public class HttpError extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -1099210397107309465L;

    private final HttpStatus status;

    private final String message;
}
