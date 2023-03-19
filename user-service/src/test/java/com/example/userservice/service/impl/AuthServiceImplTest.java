package com.example.userservice.service.impl;

import com.example.userservice.exception.ResourceAlreadyExistException;
import com.example.userservice.model.User;
import com.example.userservice.model.UserRole;
import com.example.userservice.payload.SignInRequestDto;
import com.example.userservice.payload.SignInResponseDto;
import com.example.userservice.payload.TokenExchangeRequestDto;
import com.example.userservice.payload.UserDto;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    JwtService jwtService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authManager;

    @Spy
    ModelMapper mapper;

    @InjectMocks
    AuthServiceImpl authService;


    @Captor
    ArgumentCaptor<User> userCaptor;

    @Test
    void whenSignUp_givenRegistrationDataIsValid_thenCreateNewUser() {
        // given
        UserDto userDto = UserDto.builder()
                .email("j.doe@mail.com")
                .password("password")
                .build();

        String encodedPassword = "rkep4h1etq8i";

        // when
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(Mockito.any(User.class))).then(AdditionalAnswers.returnsFirstArg());

        UserDto response = authService.signUp(userDto);

        // then
        verify(userRepository).findByEmail(userDto.getEmail());
        verify(passwordEncoder).encode(userDto.getPassword());
        verify(userRepository).save(userCaptor.capture());

        User user = userCaptor.getValue();
        assertThat(user.getFirstName(), is(user.getFirstName()));
        assertThat(user.getLastName(), is(user.getLastName()));
        assertThat(user.getEmail(), is(userDto.getEmail()));
        assertThat(user.getPassword(), is(encodedPassword));
        assertThat(user.getRole(), is(userDto.getRole()));
        assertThat(user.isEnabled(), is(true));
        assertThat(user.getCreatedAt(), is(notNullValue()));
        assertThat(user.getUpdatedAt(), is(notNullValue()));

        assertThat(response.getEmail(), is(userDto.getEmail()));
    }

    @Test
    void whenSignUp_givenEmailIsTaken_thenThrowException() {
        // given
        UserDto userDto = UserDto.builder().email("j.doe@mail.com").build();

        User otherUser = User.builder().email(userDto.getEmail()).build();

        // when
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(otherUser));

        // then
        assertThrows(ResourceAlreadyExistException.class, () -> authService.signUp(userDto));
        verify(userRepository).findByEmail(userDto.getEmail());
    }

    @Test
    void whenSignIn_givenCredentialsAreValid_thenGenerateJwt() {
        // given
        String email = "j.doe@mail.com";
        String password = "password";
        String jwt = "eyJ0eXA.eyJzdWIi.Ou-2-0gYTg";

        SignInRequestDto request = SignInRequestDto.builder()
                .email(email)
                .password(password)
                .build();


        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);

        User user = User.builder().id("1234").email("j.doe@mail.com").build();
        Authentication verified = new PreAuthenticatedAuthenticationToken(user, "");

        // when
        when(authManager.authenticate(auth)).thenReturn(verified);
        when(jwtService.createToken(user)).thenReturn(jwt);

        SignInResponseDto response = authService.signIn(request);

        // then
        verify(authManager).authenticate(auth);
        verify(jwtService).createToken(user);

        assertThat(response.getToken(), is(jwt));
    }

    @Test
    void whenSignIn_givenCredentialsAreInvalid_thenThrowException() {
        // given
        String email = "j.doe@mail.com";
        String password = "password";

        SignInRequestDto request = SignInRequestDto.builder().email(email).password(password).build();

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);

        // when
        when(authManager.authenticate(auth)).thenThrow(new BadCredentialsException("Invalid password"));

        // then
        assertThrows(BadCredentialsException.class, () -> authService.signIn(request));
        verify(authManager).authenticate(auth);
    }


    @Test
    void givenExchangeToken_whenTokenIsValidAndUserExist_thenReturnUser() {
        // given
        String token = "eyJ0eXA.eyJzdWIi.Ou-2-0gYTg";
        TokenExchangeRequestDto request = new TokenExchangeRequestDto(token);

        String userId = "1234-qwer";
        User user = User.builder().id(userId).email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        // when
        when(jwtService.decodeToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto res = authService.exchangeToken(request);

        // then
        verify(jwtService).decodeToken(token);
        verify(userRepository).findById(userId);
        verify(mapper).map(user, UserDto.class);

        assertThat(res.getId(), is(userId));
        assertThat(res.getEmail(), is(user.getEmail()));
    }

    @Test
    void givenExchangeToken_whenUserDoesntExist_thenThrowException() {
        // given
        String token = "eyJ0eXA.eyJzdWIi.Ou-2-0gYTg";
        TokenExchangeRequestDto request = new TokenExchangeRequestDto(token);

        String userId = "1234-qwer";

        // when
        when(jwtService.decodeToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // then
        assertThrows(RuntimeException.class, () -> authService.exchangeToken(request));
        verify(jwtService).decodeToken(token);
        verify(userRepository).findById(userId);
    }

    @Test
    void givenExchangeToken_whenTokenIsInvalid_thenThrowException() {
        // given
        String token = "eyJ0eXA.eyJzdWIi.Ou-2-0gYTg";
        TokenExchangeRequestDto request = new TokenExchangeRequestDto(token);

        // when
        when(jwtService.decodeToken(token)).thenThrow(new RuntimeException());

        // then
        assertThrows(RuntimeException.class, () -> authService.exchangeToken(request));
        verify(jwtService).decodeToken(token);
    }
}