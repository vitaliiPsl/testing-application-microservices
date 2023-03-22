package com.example.testservice.model;

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
@Table(name = "tests")
public class Test {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String subjectId;

    private String educatorId;

    private String name;

    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Question> questions = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
