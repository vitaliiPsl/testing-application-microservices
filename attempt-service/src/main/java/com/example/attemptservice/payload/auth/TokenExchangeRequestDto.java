package com.example.attemptservice.payload.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenExchangeRequestDto {
    @NotBlank(message = "Token is required")
    private String token;
}
