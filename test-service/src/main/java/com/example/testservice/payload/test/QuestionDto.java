package com.example.testservice.payload.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long id;

    @NotBlank(message = "Question is required")
    @Length(max = 512, message = "Question must be up to 512 characters")
    private String question;

    @Size(min = 2, message = "You need to provided at least two distinct options")
    private Set<OptionDto> options;
}
