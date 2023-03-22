package com.example.testservice.client.impl;

import com.example.testservice.exception.ResourceNotFoundException;
import com.example.testservice.payload.SubjectDto;
import com.example.testservice.payload.auth.UserDto;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectClientImplTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    SubjectClientImpl subjectClient;

    @Test
    void whenGetSubjectById_givenSubjectExist_thenReturnSubject() {
        // given
        String subjectId = "1234";
        SubjectDto subjectDto = SubjectDto.builder().id(subjectId).build();
        ResponseEntity<SubjectDto> subjectResponseEntity = new ResponseEntity<>(subjectDto, HttpStatus.OK);

        String subjectServiceUrl = "http://subject-service/api/subjects/";
        ReflectionTestUtils.setField(subjectClient, "subjectServiceUrl", subjectServiceUrl);

        // when
        when(restTemplate.getForEntity(subjectServiceUrl + "{subjectId}", SubjectDto.class, subjectId))
                .thenReturn(subjectResponseEntity);
        SubjectDto subject = subjectClient.getSubjectById(subjectId);

        // then
        verify(restTemplate).getForEntity(subjectServiceUrl + "{subjectId}", SubjectDto.class, subjectId);
        assertThat(subject.getId(), is(subjectId));
    }

    @Test
    void whenGetSubjectById_givenSubjectDoestExist_thenThrowNotFoundException() {
        // given
        String subjectId = "1234";
        ResponseEntity<SubjectDto> subjectResponseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        String subjectServiceUrl = "http://subject-service/api/subjects/";
        ReflectionTestUtils.setField(subjectClient, "subjectServiceUrl", subjectServiceUrl);

        // when
        when(restTemplate.getForEntity(subjectServiceUrl + "{subjectId}", SubjectDto.class, subjectId))
                .thenReturn(subjectResponseEntity);

        // then
        assertThrows(ResourceNotFoundException.class, () -> subjectClient.getSubjectById(subjectId));
        verify(restTemplate).getForEntity(subjectServiceUrl + "{subjectId}", SubjectDto.class, subjectId);
    }

    @Test
    void whenGetSubjectById_givenSubjectSomeError_thenThrowRuntimeException() {
        // given
        String subjectId = "1234";
        ResponseEntity<SubjectDto> subjectResponseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        String subjectServiceUrl = "http://subject-service/api/subjects/";
        ReflectionTestUtils.setField(subjectClient, "subjectServiceUrl", subjectServiceUrl);

        // when
        when(restTemplate.getForEntity(subjectServiceUrl + "{subjectId}", SubjectDto.class, subjectId))
                .thenReturn(subjectResponseEntity);

        // then
        assertThrows(RuntimeException.class, () -> subjectClient.getSubjectById(subjectId));
        verify(restTemplate).getForEntity(subjectServiceUrl + "{subjectId}", SubjectDto.class, subjectId);
    }
}