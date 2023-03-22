package com.example.testservice.client.impl;

import com.example.testservice.client.SubjectClient;
import com.example.testservice.exception.ResourceNotFoundException;
import com.example.testservice.payload.SubjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class SubjectClientImpl implements SubjectClient {
    private final RestTemplate restTemplate;

    @Value("${subjects.subject-url}")
    private String subjectServiceUrl;

    @Override
    public SubjectDto getSubjectById(String subjectId) {
        log.debug("Get subject by id: {}", subjectId);

        ResponseEntity<SubjectDto> response = restTemplate.getForEntity(subjectServiceUrl + "{subjectId}", SubjectDto.class, subjectId);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            log.error("Subject with id {} doesn't exist", subjectId);
            throw new ResourceNotFoundException("Subject", "id", subjectId);
        }

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            log.error("Something went wrong");
            throw new IllegalStateException("Something went wrong");
        }

        return response.getBody();
    }
}
