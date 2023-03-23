package com.example.attemptservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestTemplateException extends RuntimeException {
    private final HttpStatus status;

    public RestTemplateException(String message, HttpStatus status) {
        super(message);

        this.status = status;
    }

    public RestTemplateException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
