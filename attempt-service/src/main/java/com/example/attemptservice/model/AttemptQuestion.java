package com.example.attemptservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attempt_questions")
public class AttemptQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL)
    private Set<AttemptAnswer> answers = new HashSet<>();

    private Integer score;
    private Integer maxScore;
}
