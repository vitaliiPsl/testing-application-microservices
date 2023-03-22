package com.example.testservice.service.impl;

import com.example.testservice.client.SubjectClient;
import com.example.testservice.exception.ForbiddenException;
import com.example.testservice.exception.ResourceNotFoundException;
import com.example.testservice.model.Option;
import com.example.testservice.model.Question;
import com.example.testservice.model.Test;
import com.example.testservice.payload.SubjectDto;
import com.example.testservice.payload.auth.UserDto;
import com.example.testservice.payload.test.OptionDto;
import com.example.testservice.payload.test.QuestionDto;
import com.example.testservice.payload.test.TestDto;
import com.example.testservice.repository.TestRepository;
import com.example.testservice.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TestServiceImpl implements TestService {
    private static final int MIN_NUMBER_OF_OPTIONS = 2;
    private static final int MIN_NUMBER_OF_CORRECT_OPTIONS = 1;

    private final TestRepository testRepository;
    private final SubjectClient subjectClient;

    private final ModelMapper mapper;

    @Override
    public TestDto saveTest(TestDto req, UserDto user) {
        log.debug("Save test {}", req);

        // get subject
        SubjectDto subject = subjectClient.getSubjectById(req.getSubjectId());
        if(!subject.getEducatorId().equals(user.getId())) {
            log.error("User is not an educator of the subject {}", subject.getId());
            throw new IllegalStateException("User is not an educator of the subject");
        }

        // map test dto to test
        Test test = createTest(req, subject);

        // map question dtos to questions
        Set<Question> questions = createQuestions(req.getQuestions());
        test.setQuestions(questions);

        // save test
        test = testRepository.save(test);
        return mapTestToTestDto(test);
    }

    @Override
    public TestDto updateTest(String testId, TestDto req, UserDto user) {
        log.debug("Update test with id {}. Update data: {}", testId, req);

        // fetch test
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("test", "id", testId));

        // verify that user is the educator of the test subject
        if(!test.getEducatorId().equals(user.getId())) {
            log.error("User is not an educator of the subject: {}", test.getSubjectId());
            throw new ForbiddenException("Not an educator of the subject: " + test.getSubjectId());
        }

        // update test properties
        test.setName(req.getName());
        test.setUpdatedAt(LocalDateTime.now());

        // map questions
        Set<Question> questions = createQuestions(req.getQuestions());
        test.setQuestions(questions);

        // save updated test
        test = testRepository.save(test);
        return mapTestToTestDto(test);
    }

    @Override
    public void deleteTest(String testId, UserDto user) {
        log.debug("Delete test with id {}", testId);

        // fetch test
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("test", "id", testId));

        // verify that user is the educator of the test subject
        if(!test.getEducatorId().equals(user.getId())) {
            log.error("User is not an educator of the subject: {}", test.getSubjectId());
            throw new ForbiddenException("Not an educator of the subject: " + test.getSubjectId());
        }

        testRepository.delete(test);
    }

    @Override
    public TestDto getTestById(String testId) {
        log.debug("Get test with id: {}", testId);

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("test", "id", testId));

        return mapTestToTestDto(test);
    }

    @Override
    public List<TestDto> getTestsBySubjectId(String subjectId) {
        log.debug("Get tests by subject with id: {}", subjectId);

        return testRepository.findBySubjectId(subjectId)
                .stream().map(this::mapTestToTestDto).collect(Collectors.toList());
    }

    private static Test createTest(TestDto testDto, SubjectDto subject) {
        return Test.builder()
                .subjectId(subject.getId())
                .educatorId(subject.getEducatorId())
                .name(testDto.getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Set<Question> createQuestions(Set<QuestionDto> questionDtos) {
        return questionDtos.stream().map(this::createQuestion).collect(Collectors.toSet());
    }

    private Question createQuestion(QuestionDto questionDto) {
        Question question = Question.builder()
                .question(questionDto.getQuestion())
                .build();

        Set<Option> options = createOptions(questionDto.getOptions());
        if (options.size() < MIN_NUMBER_OF_OPTIONS) {
            log.error("Requires at least {} option. Received: {}", MIN_NUMBER_OF_OPTIONS, options.size());
            throw new IllegalStateException(
                    String.format("There must be at least %s option for question: '%s'", MIN_NUMBER_OF_OPTIONS, question.getQuestion())
            );
        }

        question.setOptions(options);
        if (question.getCorrectOptions().size() < 1) {
            log.error("Requires at least one correct option. Received: {}", question.getCorrectOptions().size());
            throw new IllegalStateException(
                    String.format("There must be at least %s correct option for question: '%s'", MIN_NUMBER_OF_CORRECT_OPTIONS, question.getQuestion())
            );
        }

        return question;
    }

    private Set<Option> createOptions(Set<OptionDto> optionDtos) {
        return optionDtos.stream().map(this::createOption).collect(Collectors.toSet());
    }

    private Option createOption(OptionDto optionDto) {
        return Option.builder()
                .option(optionDto.getOption())
                .correct(optionDto.isCorrect())
                .build();
    }

    private TestDto mapTestToTestDto(Test test) {
        return mapper.map(test, TestDto.class);
    }
}
