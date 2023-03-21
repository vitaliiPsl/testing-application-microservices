package com.example.subjectservice.client.impl;

import com.example.subjectservice.client.AuthClient;
import com.example.subjectservice.payload.auth.TokenExchangeRequestDto;
import com.example.subjectservice.payload.auth.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthClientImpl implements AuthClient {
    private final RestTemplate restTemplate;

    @Value("${auth.token-exchange-url}")
    private String tokenExchangeUrl;

    @Override
    public UserDto exchangeToken(TokenExchangeRequestDto request) {
        log.debug("Exchange token");

        ResponseEntity<UserDto> response = restTemplate.postForEntity(tokenExchangeUrl, request, UserDto.class);
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            log.error("Unauthorized");
            throw new BadCredentialsException("Unauthorized");
        }

        return response.getBody();
    }
}
