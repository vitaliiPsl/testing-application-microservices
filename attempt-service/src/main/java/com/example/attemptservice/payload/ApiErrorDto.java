package com.example.attemptservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorDto {
    private String message;

    private HttpStatus status;

    private String error;

    public ApiErrorDto(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiErrorDto(HttpStatus status, String message, Throwable throwable) {
        this.status = status;
        this.message = message;
        this.error = throwable.getMessage();
    }
}
