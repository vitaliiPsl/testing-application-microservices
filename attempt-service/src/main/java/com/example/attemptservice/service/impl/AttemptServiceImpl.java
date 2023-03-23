package com.example.attemptservice.service.impl;

import com.example.attemptservice.client.TestClient;
import com.example.attemptservice.exception.ResourceNotFoundException;
import com.example.attemptservice.model.AttemptAnswer;
import com.example.attemptservice.model.AttemptQuestion;
import com.example.attemptservice.model.AttemptResult;
import com.example.attemptservice.payload.attempt.AttemptAnswerDto;
import com.example.attemptservice.payload.attempt.AttemptDto;
import com.example.attemptservice.payload.attempt.AttemptQuestionDto;
import com.example.attemptservice.payload.attempt.AttemptResultDto;
import com.example.attemptservice.payload.auth.UserDto;
import com.example.attemptservice.payload.test.OptionDto;
import com.example.attemptservice.payload.test.QuestionDto;
import com.example.attemptservice.payload.test.TestDto;
import com.example.attemptservice.repository.AttemptResultRepository;
import com.example.attemptservice.service.AttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AttemptServiceImpl implements AttemptService {
    private final AttemptResultRepository attemptRepository;
    private final TestClient testClient;
    private final ModelMapper mapper;

    @Override
    public AttemptResultDto processAttempt(AttemptDto attemptDto, UserDto user) {
        log.debug("Process attempt of the test with id {}. Attempt details: {}", attemptDto.getTestId(), attemptDto);

        // find test
        TestDto test = testClient.getTestById(attemptDto.getTestId());
        System.out.println(test);

        Set<QuestionDto> questions = test.getQuestions();

        // check answers
        Set<AttemptQuestion> attemptQuestions = checkQuestionsAnswers(questions, List.copyOf(attemptDto.getQuestions()));

        AttemptResult attempt = AttemptResult.builder()
                .userId(user.getId())
                .testId(test.getId())
                .createdAt(LocalDateTime.now())
                .build();

        // get score and assign attempt to attempt questions
        int score = 0;
        int maxScore = 0;
        for (var question : attemptQuestions) {
            score += question.getScore();
            maxScore += question.getMaxScore();
        }

        attempt.setAttemptQuestions(attemptQuestions);
        attempt.setScore(score);
        attempt.setMaxScore(maxScore);

        // save attempt
        attempt = attemptRepository.save(attempt);
        return mapAttemptResultToAttemptResultDto(attempt);
    }

    @Override
    public AttemptResultDto getAttemptById(String attemptId) {
        log.debug("Get attempt by id {}", attemptId);

        AttemptResult attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", "id", attemptId));

        return mapAttemptResultToAttemptResultDto(attempt);
    }

    @Override
    public List<AttemptResultDto> getAttemptsByTestId(String testId) {
        log.debug("Get attempts by id of the test: {}", testId);

        return attemptRepository.findByTestId(testId)
                .stream().map(this::mapAttemptResultToAttemptResultDto).collect(Collectors.toList());
    }

    @Override
    public List<AttemptResultDto> getAttemptsByUserId(String userId) {
        log.debug("Get attempts by user: {}", userId);

        return attemptRepository.findByUserId(userId)
                .stream().map(this::mapAttemptResultToAttemptResultDto).collect(Collectors.toList());
    }

    private Set<AttemptQuestion> checkQuestionsAnswers(Set<QuestionDto> questions, List<AttemptQuestionDto> attemptQuestions) {
        return questions.stream()
                .map(question -> checkQuestionAnswer(question, attemptQuestions))
                .collect(Collectors.toSet());
    }

    private AttemptQuestion checkQuestionAnswer(QuestionDto question, List<AttemptQuestionDto> attemptQuestions) {
        // get attempt for given question
        Optional<AttemptQuestionDto> questionAttemptDto = attemptQuestions.stream()
                .filter(attemptQuestion -> question.getId().equals(attemptQuestion.getQuestionId())).findFirst();

        // get answers for given question
        Set<AttemptAnswer> answers = Set.of();
        if (questionAttemptDto.isPresent()) {
            answers = checkQuestionAnswer(question, questionAttemptDto.get());
        }

        AttemptQuestion attemptQuestion = AttemptQuestion.builder().questionId(question.getId())
                .maxScore(getQuestionCorrectOptions(question).size()).build();

        // set answer question and count correct answers
        int correctAnswers = 0;
        for (var answer : answers) {
            correctAnswers += answer.isCorrect() ? 1 : 0;
        }

        attemptQuestion.setAnswers(answers);
        attemptQuestion.setScore(correctAnswers);

        return attemptQuestion;
    }

    private Set<AttemptAnswer> checkQuestionAnswer(QuestionDto question, AttemptQuestionDto attemptQuestion) {
        Set<AttemptAnswerDto> attemptQuestionAnswers = attemptQuestion.getAnswers();

        List<OptionDto> correctOptions = getQuestionCorrectOptions(question);

        if (attemptQuestionAnswers.size() > correctOptions.size()) {
            log.error("Possible number of answers: {}, received {}", correctOptions.size(), attemptQuestionAnswers.size());
            throw new IllegalStateException("Invalid number of answers. Require no more than " + correctOptions.size());
        }

        // collect options to map with ids as keys and options as values
        Map<Long, OptionDto> options = question.getOptions().stream()
                .collect(Collectors.toMap(OptionDto::getId, Function.identity()));

        return attemptQuestionAnswers.stream()
                .map(answer -> checkQuestionAnswer(answer, options)).collect(Collectors.toSet());
    }

    private AttemptAnswer checkQuestionAnswer(AttemptAnswerDto answer, Map<Long, OptionDto> options) {
        OptionDto option = options.get(answer.getOptionId());

        if (option == null) {
            log.error("There is not such option for given question: {}", answer.getOptionId());
            throw new IllegalStateException("No such option: " + answer.getOptionId());
        }

        return AttemptAnswer.builder()
                .optionId(option.getId())
                .correct(option.isCorrect())
                .build();
    }

    private List<OptionDto> getQuestionCorrectOptions(QuestionDto question) {
        return question.getOptions().stream().filter(OptionDto::isCorrect).collect(Collectors.toList());
    }

    private AttemptResultDto mapAttemptResultToAttemptResultDto(AttemptResult attemptResult) {
        return mapper.map(attemptResult, AttemptResultDto.class);
    }
}
