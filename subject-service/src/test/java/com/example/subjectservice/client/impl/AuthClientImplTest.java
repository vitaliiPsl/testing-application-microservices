package com.example.subjectservice.client.impl;

import com.example.subjectservice.payload.auth.TokenExchangeRequestDto;
import com.example.subjectservice.payload.auth.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class AuthClientImplTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    AuthClientImpl authClient;

    @Test
    void whenExchangeToken_givenValidRequest_thenReturnFetchedUserDto() {
        // given

        String token = "1234-qwer-4567";
        TokenExchangeRequestDto tokenExchangeRequest = new TokenExchangeRequestDto(token);

        UserDto user = UserDto.builder().id("1234").email("j.doe@mail.com").build();
        ResponseEntity<UserDto> userResponseEntity = new ResponseEntity<>(user, HttpStatus.OK);

        String exchangeTokenUrl = "http://auth-service/api/token";
        ReflectionTestUtils.setField(authClient, "tokenExchangeUrl", exchangeTokenUrl);

        // when
        when(restTemplate.postForEntity(exchangeTokenUrl, tokenExchangeRequest, UserDto.class)).thenReturn(userResponseEntity);

        UserDto res = authClient.exchangeToken(tokenExchangeRequest);

        // then
        verify(restTemplate).postForEntity(exchangeTokenUrl, tokenExchangeRequest, UserDto.class);
        assertThat(res.getId(), is(user.getId()));
    }

    @Test
    void whenExchangeToken_givenUnauthorized_thenThrowException() {
        // given

        String token = "1234-qwer-4567";
        TokenExchangeRequestDto tokenExchangeRequest = new TokenExchangeRequestDto(token);

        ResponseEntity<UserDto> userResponseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String exchangeTokenUrl = "http://auth-service/api/token";
        ReflectionTestUtils.setField(authClient, "tokenExchangeUrl", exchangeTokenUrl);

        // when
        when(restTemplate.postForEntity(exchangeTokenUrl, tokenExchangeRequest, UserDto.class)).thenReturn(userResponseEntity);

        // then
        assertThrows(RuntimeException.class, () -> authClient.exchangeToken(tokenExchangeRequest));
        verify(restTemplate).postForEntity(exchangeTokenUrl, tokenExchangeRequest, UserDto.class);
    }
}