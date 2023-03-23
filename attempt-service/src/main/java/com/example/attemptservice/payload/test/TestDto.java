package com.example.attemptservice.payload.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {

    private String id;

    private String subjectId;

    private String educatorId;

    private String name;

    private Set<QuestionDto> questions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
