package com.example.userservice.service.impl;

import com.example.userservice.exception.ResourceAlreadyExistException;
import com.example.userservice.model.User;
import com.example.userservice.payload.UserDto;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final PasswordEncoder passwordEncoder;
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
