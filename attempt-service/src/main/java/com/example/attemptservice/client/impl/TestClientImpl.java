package com.example.attemptservice.client.impl;

import com.example.attemptservice.client.TestClient;
import com.example.attemptservice.payload.test.TestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestClientImpl implements TestClient {
    private final RestTemplate restTemplate;

    @Value("${tests.test-url}")
    private String testServiceUrl;

    @Override
    public TestDto getTestById(String testId) {
        log.debug("Get test by id: {}", testId);

        return restTemplate.getForEntity(testServiceUrl + "{testId}", TestDto.class, testId).getBody();
    }
}
