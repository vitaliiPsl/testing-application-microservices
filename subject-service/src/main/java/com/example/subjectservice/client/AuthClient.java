package com.example.subjectservice.client;

import com.example.subjectservice.payload.auth.TokenExchangeRequestDto;
import com.example.subjectservice.payload.auth.UserDto;

public interface AuthClient {
    UserDto exchangeToken(TokenExchangeRequestDto request);
}
