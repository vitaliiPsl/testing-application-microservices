package com.example.attemptservice.payload.attempt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResultDto {
    private String id;

    private String userId;

    private String testId;

    private Set<AttemptQuestionDto> attemptQuestions = new HashSet<>();

    private Integer score;

    private Integer maxScore;

    private LocalDateTime createdAt;
}
