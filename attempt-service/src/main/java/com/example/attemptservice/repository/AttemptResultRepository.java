package com.example.attemptservice.repository;

import com.example.attemptservice.model.AttemptResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptResultRepository extends JpaRepository<AttemptResult, String> {

    List<AttemptResult> findByUserId(String userId);

    List<AttemptResult> findByTestId(String testId);

    List<AttemptResult> findByTestIdAndUserId(String testId, String userId);
}
