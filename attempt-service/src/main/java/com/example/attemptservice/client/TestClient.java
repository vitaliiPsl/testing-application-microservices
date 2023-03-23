package com.example.attemptservice.client;

import com.example.attemptservice.payload.test.TestDto;

public interface TestClient {
     TestDto getTestById(String testId);
}
