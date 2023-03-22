package com.example.testservice.client;

import com.example.testservice.payload.auth.TokenExchangeRequestDto;
import com.example.testservice.payload.auth.UserDto;

public interface AuthClient {
    UserDto exchangeToken(TokenExchangeRequestDto request);
}
