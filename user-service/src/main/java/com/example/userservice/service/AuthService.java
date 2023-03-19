package com.example.userservice.service;

import com.example.userservice.payload.SignInRequestDto;
import com.example.userservice.payload.SignInResponseDto;
import com.example.userservice.payload.TokenExchangeRequestDto;
import com.example.userservice.payload.UserDto;

/**
 * Authentication service
 */
public interface AuthService {

    /**
     * Register a new user
     *
     * @param userDto user to register
     * @return registered user
     */
    UserDto signUp(UserDto userDto);

    /**
     * Authenticate the user with given credentials
     *
     * @param request credentials of the user
     * @return sign in response that contains the JWT token
     */
    SignInResponseDto signIn(SignInRequestDto request);

    /**
     * Exchange jwt token to user dto
     *
     * @param request jwt token exchange request
     * @return user to whom the token belongs
     */
    UserDto exchangeToken(TokenExchangeRequestDto request);
}
