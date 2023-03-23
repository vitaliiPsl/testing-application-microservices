package com.example.attemptservice.client.impl;

import com.example.attemptservice.payload.test.TestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestClientImplTest {
    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    TestClientImpl testClient;

    @Test
    void whenGetTestById_givenTestExist_thenReturnTest() {
        // given
        String testId = "1234";
        TestDto test = TestDto.builder().id(testId).build();

        ResponseEntity<TestDto> testResponseEntity = new ResponseEntity<>(test, HttpStatus.OK);

        String testServiceUrl = "http://subject-service/api/subjects/";
        ReflectionTestUtils.setField(testClient, "testServiceUrl", testServiceUrl);

        // when
        when(restTemplate.getForEntity(testServiceUrl + "{testId}", TestDto.class, testId))
                .thenReturn(testResponseEntity);

        TestDto res = testClient.getTestById(testId);

        // then
        verify(restTemplate).getForEntity(testServiceUrl + "{testId}", TestDto.class, testId);
        assertThat(res.getId(), is(testId));
    }
}