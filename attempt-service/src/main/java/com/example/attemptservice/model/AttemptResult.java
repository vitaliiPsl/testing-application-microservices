package com.example.attemptservice.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attempts")
public class AttemptResult {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String userId;

    private String testId;

    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL)
    private Set<AttemptQuestion> attemptQuestions = new HashSet<>();

    private Integer score;

    private Integer maxScore;

    private LocalDateTime createdAt;
}
