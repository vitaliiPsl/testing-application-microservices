package com.example.testservice.payload.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {

    private String id;

    @NotBlank(message = "Id of the subject is required")
    private String subjectId;

    private String educatorId;

    @NotBlank(message = "Test name is required")
    @Length(max = 255, message = "Test name must be up to 512 characters")
    private String name;

    @Size(min = 2, message = "Test must contain at least two distinct questions")
    private Set<QuestionDto> questions;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
