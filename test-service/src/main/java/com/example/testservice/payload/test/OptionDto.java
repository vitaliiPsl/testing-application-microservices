package com.example.testservice.payload.test;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {

    private Long id;

    @NotBlank(message = "Option is required")
    @Length(max = 512, message = "Option must be up to 512 characters")
    private String option;

    @EqualsAndHashCode.Exclude
    private boolean correct;
}
