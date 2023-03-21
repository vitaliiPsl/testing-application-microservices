package com.example.subjectservice.repository;

import com.example.subjectservice.model.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {
    List<Subject> findAllByEducatorId(String educatorId);
}
