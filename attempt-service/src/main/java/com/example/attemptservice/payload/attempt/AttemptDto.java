package com.example.attemptservice.payload.attempt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDto {

    @NotBlank(message = "Id of the test is required")
    private String testId;

    private Set<AttemptQuestionDto> questions = new HashSet<>();
}
