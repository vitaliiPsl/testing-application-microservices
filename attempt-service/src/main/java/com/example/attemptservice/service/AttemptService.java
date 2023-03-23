package com.example.attemptservice.service;

import com.example.attemptservice.payload.attempt.AttemptDto;
import com.example.attemptservice.payload.attempt.AttemptResultDto;
import com.example.attemptservice.payload.auth.UserDto;

import java.util.List;

public interface AttemptService {
    /**
     * Process test attempt and return result
     *
     * @param attemptDto attempt details
     * @param user       authenticated user
     * @return attempt result
     */
    AttemptResultDto processAttempt(AttemptDto attemptDto, UserDto user);

    /**
     * Get attempt result by id
     *
     * @param attemptId if of the attempt
     * @return fetched attempt
     */
    AttemptResultDto getAttemptById(String attemptId);

    /**
     * Get attempts by id of the test
     *
     * @param testId id of the test
     * @return list of attempt
     */
    List<AttemptResultDto> getAttemptsByTestId(String testId);

    /**
     * Get attempts by id of the user
     *
     * @param userId id of the user
     * @return list of attempts
     */
    List<AttemptResultDto> getAttemptsByUserId(String userId);
}
