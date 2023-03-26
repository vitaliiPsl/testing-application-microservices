package com.example.attemptservice.payload.attempt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptQuestionDto {

    private Long questionId;

    private Set<AttemptAnswerDto> answers = new HashSet<>();

    private Integer score;

    private Integer maxScore;
}

