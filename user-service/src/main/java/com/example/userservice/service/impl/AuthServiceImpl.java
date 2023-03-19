package com.example.userservice.service.impl;

import com.example.userservice.exception.ResourceAlreadyExistException;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.model.User;
import com.example.userservice.payload.SignInRequestDto;
import com.example.userservice.payload.SignInResponseDto;
import com.example.userservice.payload.TokenExchangeRequestDto;
import com.example.userservice.payload.UserDto;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.AuthService;
import com.example.userservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final ModelMapper mapper;

    @Override
    public UserDto signUp(UserDto userDto) {
        log.debug("Register new user: {}", userDto);

        // check if email is available
        Optional<User> existing = userRepository.findByEmail(userDto.getEmail());
        if (existing.isPresent()) {
            log.error("Email '{}' is already taken", userDto.getEmail());
            throw new ResourceAlreadyExistException("User", "email", userDto.getEmail());
        }

        // save user
        User user = createUser(userDto);
        userRepository.save(user);

        return mapUserToUserDto(user);
    }

    @Override
    public SignInResponseDto signIn(SignInRequestDto request) {
        log.debug("Authenticate user: {}", request.getEmail());

        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        authentication = authManager.authenticate(authentication);

        // authentication principal
        User user = (User) authentication.getPrincipal();

        String token = jwtService.createToken(user);
        return new SignInResponseDto(token);
    }

    @Override
    public UserDto exchangeToken(TokenExchangeRequestDto request) {
        log.debug("Exchange token: {}", request.getToken().substring(0, 10));

        String token = request.getToken();
        String userId = jwtService.decodeToken(token);

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.error("User with id '{}' doesn't exist", userId);
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return mapUserToUserDto(user.get());
    }

    private User createUser(UserDto userDto) {
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(encodedPassword)
                .role(userDto.getRole())
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private UserDto mapUserToUserDto(User user) {
        return mapper.map(user, UserDto.class);
    }
}
