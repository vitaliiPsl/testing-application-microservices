package com.example.subjectservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    private String id;

    private String educatorId;

    @NotBlank(message = "Name of the subject is required")
    private String name;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
