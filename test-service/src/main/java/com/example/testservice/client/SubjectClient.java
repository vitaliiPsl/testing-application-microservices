package com.example.testservice.client;

import com.example.testservice.payload.SubjectDto;

public interface SubjectClient {
     SubjectDto getSubjectById(String subjectId);
}
