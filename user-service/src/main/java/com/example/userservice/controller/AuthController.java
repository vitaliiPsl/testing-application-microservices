package com.example.userservice.controller;

import com.example.userservice.payload.SignInRequestDto;
import com.example.userservice.payload.SignInResponseDto;
import com.example.userservice.payload.TokenExchangeRequestDto;
import com.example.userservice.payload.UserDto;
import com.example.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("signup")
    UserDto signUp(@RequestBody @Valid UserDto req) {
        return authService.signUp(req);
    }

    @PostMapping("signin")
    SignInResponseDto signUp(@RequestBody @Valid SignInRequestDto req) {
        return authService.signIn(req);
    }

    @PostMapping("token")
    UserDto exchangeToken(@RequestBody @Valid TokenExchangeRequestDto req) {
        return authService.exchangeToken(req);
    }
}
