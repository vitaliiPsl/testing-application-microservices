package com.example.attemptservice.payload.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {

    private Long id;

    private String option;

    private boolean correct;
}
