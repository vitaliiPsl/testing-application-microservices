package com.example.userservice.service.impl;

import com.example.userservice.exception.ResourceAlreadyExistException;
import com.example.userservice.model.User;
import com.example.userservice.payload.UserDto;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    PasswordEncoder passwordEncoder;

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
}