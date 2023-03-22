package com.example.testservice.controller;

import com.example.testservice.payload.auth.UserDto;
import com.example.testservice.payload.test.TestDto;
import com.example.testservice.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tests")
public class TestController {
    private final TestService testService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    TestDto saveTest(
            @RequestBody @Valid TestDto req, @AuthenticationPrincipal UserDto user
    ) {
        return testService.saveTest(req, user);
    }

    @PutMapping("{testId}")
    TestDto updateTest(
            @PathVariable String testId,
            @RequestBody @Valid TestDto req, @AuthenticationPrincipal UserDto user
    ) {
        return testService.updateTest(testId, req, user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{testId}")
    void deleteTest(
            @PathVariable String testId,
            @AuthenticationPrincipal UserDto user
    ) {
        testService.deleteTest(testId, user);
    }

    @GetMapping("{testId}")
    TestDto getTests(@PathVariable String testId) {
        return testService.getTestById(testId);
    }

    @GetMapping(params = "subjectId")
    List<TestDto> getTestBySubjectId(@RequestParam String subjectId) {
        return testService.getTestsBySubjectId(subjectId);
    }
}
