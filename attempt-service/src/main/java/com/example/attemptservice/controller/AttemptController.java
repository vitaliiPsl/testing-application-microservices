package com.example.attemptservice.controller;

import com.example.attemptservice.payload.attempt.AttemptDto;
import com.example.attemptservice.payload.attempt.AttemptResultDto;
import com.example.attemptservice.payload.auth.UserDto;
import com.example.attemptservice.service.AttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/attempts")
public class AttemptController {
    private final AttemptService attemptService;

    @PostMapping
    AttemptResultDto saveAttempt(@RequestBody @Valid AttemptDto attemptDto, @AuthenticationPrincipal UserDto userDto) {
        return attemptService.processAttempt(attemptDto, userDto);
    }

    @GetMapping("{attemptId}")
    AttemptResultDto getAttemptById(@PathVariable String attemptId) {
        return attemptService.getAttemptById(attemptId);
    }

    @GetMapping
    List<AttemptResultDto> getAttempts(@RequestParam(required = false) String userId, @RequestParam(required = false) String testId) {
        if(userId != null) {
            return attemptService.getAttemptsByUserId(userId);
        }

        if (testId != null) {
            return attemptService.getAttemptsByTestId(testId);
        }

        return List.of();
    }
}
