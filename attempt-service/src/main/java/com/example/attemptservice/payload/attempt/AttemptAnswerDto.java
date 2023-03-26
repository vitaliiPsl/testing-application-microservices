package com.example.attemptservice.payload.attempt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAnswerDto {
    private Long optionId;

    private boolean correct;
}
