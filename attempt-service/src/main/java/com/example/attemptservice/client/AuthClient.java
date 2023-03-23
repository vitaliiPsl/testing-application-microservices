package com.example.attemptservice.client;

import com.example.attemptservice.payload.auth.TokenExchangeRequestDto;
import com.example.attemptservice.payload.auth.UserDto;

public interface AuthClient {
    UserDto exchangeToken(TokenExchangeRequestDto request);
}
