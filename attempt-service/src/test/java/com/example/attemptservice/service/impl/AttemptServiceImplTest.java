package com.example.attemptservice.service.impl;

import com.example.attemptservice.client.TestClient;
import com.example.attemptservice.exception.ResourceNotFoundException;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttemptServiceImplTest {
    @Mock
    AttemptResultRepository attemptRepository;
    @Mock
    TestClient testClient;
    @Spy
    ModelMapper mapper;

    @InjectMocks
    AttemptServiceImpl attemptService;

    @Captor
    ArgumentCaptor<AttemptResult> attemptCaptor;

    @org.junit.jupiter.api.Test
    void whenProcessAttempt_givenAllAnswersCorrect_thenSaveAttemptResultWithMaxScore() {
        // given
        String testId = "qwer-1234";

        TestDto test = buildTest(testId);

        UserDto user = UserDto.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of(AttemptAnswerDto.builder().optionId(2L).build()))
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of(
                        AttemptAnswerDto.builder().optionId(4L).build(),
                        AttemptAnswerDto.builder().optionId(5L).build()
                ))
                .build();

        AttemptDto attemptDto = AttemptDto.builder().testId(testId).questions(Set.of(question1, question2)).build();

        // when
        when(testClient.getTestById(testId)).thenReturn(test);
        when(attemptRepository.save(Mockito.any(AttemptResult.class))).then(AdditionalAnswers.returnsFirstArg());

        AttemptResultDto res = attemptService.processAttempt(attemptDto, user);

        // then
        verify(testClient).getTestById(testId);
        verify(attemptRepository).save(attemptCaptor.capture());

        AttemptResult attemptResult = attemptCaptor.getValue();
        assertThat(attemptResult.getUserId(), is(user.getId()));
        assertThat(attemptResult.getTestId(), is(test.getId()));
        assertThat(attemptResult.getScore(), is(3));
        assertThat(attemptResult.getMaxScore(), is(3));
        assertThat(attemptResult.getAttemptQuestions(), hasSize(2));
        assertThat(attemptResult.getCreatedAt(), is(notNullValue()));

        assertThat(res.getScore(), is(3));
        assertThat(res.getMaxScore(), is(3));
        assertThat(res.getAttemptQuestions(), hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt_givenOnlyOneCorrectAnswerToQuestionWithTwoCorrectAnswers_thenSaveAttemptResultWithScore2OutOf3() {
        // given
        String testId = "qwer-1234";

        TestDto test = buildTest(testId);

        UserDto user = UserDto.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of(AttemptAnswerDto.builder().optionId(2L).build()))
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of(
                        AttemptAnswerDto.builder().optionId(3L).build(),
                        AttemptAnswerDto.builder().optionId(5L).build()
                ))
                .build();

        AttemptDto attemptDto = AttemptDto.builder().testId(testId).questions(Set.of(question1, question2)).build();

        // when
        when(testClient.getTestById(testId)).thenReturn(test);
        when(attemptRepository.save(Mockito.any(AttemptResult.class))).then(AdditionalAnswers.returnsFirstArg());

        AttemptResultDto res = attemptService.processAttempt(attemptDto, user);

        // then
        verify(testClient).getTestById(testId);
        verify(attemptRepository).save(attemptCaptor.capture());

        AttemptResult attemptResult = attemptCaptor.getValue();
        assertThat(attemptResult.getUserId(), is(user.getId()));
        assertThat(attemptResult.getTestId(), is(test.getId()));
        assertThat(attemptResult.getScore(), is(2));
        assertThat(attemptResult.getMaxScore(), is(3));
        assertThat(attemptResult.getAttemptQuestions(), hasSize(2));
        assertThat(attemptResult.getCreatedAt(), is(notNullValue()));

        assertThat(res.getScore(), is(2));
        assertThat(res.getMaxScore(), is(3));
        assertThat(res.getAttemptQuestions(), hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt_givenAllAnswersWrong_thenSaveAttemptResultWithZeroScore() {
        // given
        String testId = "qwer-1234";

        TestDto test = buildTest(testId);

        UserDto user = UserDto.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of(AttemptAnswerDto.builder().optionId(1L).build()))
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of(
                        AttemptAnswerDto.builder().optionId(3L).build()
                ))
                .build();

        AttemptDto attemptDto = AttemptDto.builder().testId(testId).questions(Set.of(question1, question2)).build();

        // when
        when(testClient.getTestById(testId)).thenReturn(test);
        when(attemptRepository.save(Mockito.any(AttemptResult.class))).then(AdditionalAnswers.returnsFirstArg());

        AttemptResultDto res = attemptService.processAttempt(attemptDto, user);

        // then
        verify(testClient).getTestById(testId);
        verify(attemptRepository).save(attemptCaptor.capture());

        AttemptResult attemptResult = attemptCaptor.getValue();
        assertThat(attemptResult.getUserId(), is(user.getId()));
        assertThat(attemptResult.getTestId(), is(test.getId()));
        assertThat(attemptResult.getScore(), is(0));
        assertThat(attemptResult.getMaxScore(), is(3));
        assertThat(attemptResult.getAttemptQuestions(), hasSize(2));
        assertThat(attemptResult.getCreatedAt(), is(notNullValue()));

        assertThat(res.getScore(), is(0));
        assertThat(res.getMaxScore(), is(3));
        assertThat(res.getAttemptQuestions(), hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt_givenAnswers_thenSaveAttemptResultWithZeroScore() {
        // given
        String testId = "qwer-1234";

        TestDto test = buildTest(testId);

        UserDto user = UserDto.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of())
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of())
                .build();

        AttemptDto attemptDto = AttemptDto.builder().testId(testId).questions(Set.of(question1, question2)).build();

        // when
        when(testClient.getTestById(testId)).thenReturn(test);
        when(attemptRepository.save(Mockito.any(AttemptResult.class))).then(AdditionalAnswers.returnsFirstArg());

        AttemptResultDto res = attemptService.processAttempt(attemptDto, user);

        // then
        verify(testClient).getTestById(testId);
        verify(attemptRepository).save(attemptCaptor.capture());

        AttemptResult attemptResult = attemptCaptor.getValue();
        assertThat(attemptResult.getUserId(), is(user.getId()));
        assertThat(attemptResult.getTestId(), is(test.getId()));
        assertThat(attemptResult.getScore(), is(0));
        assertThat(attemptResult.getMaxScore(), is(3));
        assertThat(attemptResult.getAttemptQuestions(), hasSize(2));
        assertThat(attemptResult.getCreatedAt(), is(notNullValue()));

        assertThat(res.getScore(), is(0));
        assertThat(res.getMaxScore(), is(3));
        assertThat(res.getAttemptQuestions(), hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt_givenMoreAnswersThanPossibleNumberOfCorrectAnswers_thenThrowException() {
        // given
        String testId = "qwer-1234";

        TestDto test = buildTest(testId);

        UserDto user = UserDto.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of(AttemptAnswerDto.builder().optionId(2L).build()))
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of(
                        AttemptAnswerDto.builder().optionId(3L).build(),
                        AttemptAnswerDto.builder().optionId(4L).build(),
                        AttemptAnswerDto.builder().optionId(5L).build() // requires two answers, given 3
                ))
                .build();

        AttemptDto attemptDto = AttemptDto.builder().testId(testId).questions(Set.of(question1, question2)).build();

        // when
        when(testClient.getTestById(testId)).thenReturn(test);

        // then
        assertThrows(IllegalStateException.class, () -> attemptService.processAttempt(attemptDto, user));
        verify(testClient).getTestById(testId);
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt_givenWrongOption_thenThrowException() {
        // given
        String testId = "qwer-1234";

        TestDto test = buildTest(testId);

        UserDto user = UserDto.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of(AttemptAnswerDto.builder().optionId(2L).build()))
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of(
                        AttemptAnswerDto.builder().optionId(3L).build(),
                        AttemptAnswerDto.builder().optionId(6L).build() // wrong option with id 6
                ))
                .build();

        AttemptDto attemptDto = AttemptDto.builder().testId(testId).questions(Set.of(question1, question2)).build();

        // when
        when(testClient.getTestById(testId)).thenReturn(test);

        // then
        assertThrows(IllegalStateException.class, () -> attemptService.processAttempt(attemptDto, user));
        verify(testClient).getTestById(testId);
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt_givenTestDoesntExist_thenThrowException() {
        // given
        String testId = "qwer-1234";

        UserDto user = UserDto.builder().id("1234").email("j.doe@mail.com").build();

        AttemptDto attemptDto = AttemptDto.builder().testId(testId).questions(Set.of()).build();

        // when
        when(testClient.getTestById(testId)).thenThrow(ResourceNotFoundException.class);

        // then
        assertThrows(ResourceNotFoundException.class, () -> attemptService.processAttempt(attemptDto, user));
        verify(testClient).getTestById(testId);
    }

    @org.junit.jupiter.api.Test
    void whenGetAttemptById_givenAttemptExist_thenReturnAttempt() {
        // given
        String attemptId = "1234-qwer";

        AttemptResult attemptResult = AttemptResult.builder()
                .id(attemptId)
                .score(10).build();

        // when
        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attemptResult));
        AttemptResultDto res = attemptService.getAttemptById(attemptId);

        // then
        verify(attemptRepository).findById(attemptId);
        assertThat(res.getId(), is(attemptId));
        assertThat(res.getScore(), is(attemptResult.getScore()));
    }

    @org.junit.jupiter.api.Test
    void whenGetAttemptById_givenAttemptDoesntExist_thenThrowNotFoundException() {
        // given
        String attemptId = "1234-qwer";

        // when
        when(attemptRepository.findById(attemptId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> attemptService.getAttemptById(attemptId));
        verify(attemptRepository).findById(attemptId);
    }

    @org.junit.jupiter.api.Test
    void whenGetAttemptByTestId_thenReturnAttempts() {
        // given
        String testId = "1234-qwer";

        List<AttemptResult> attempts = List.of(
                AttemptResult.builder().id("1234").testId(testId).score(10).build(),
                AttemptResult.builder().id("4321").testId(testId).score(10).build()
        );

        // when
        when(attemptRepository.findByTestId(testId)).thenReturn(attempts);
        List<AttemptResultDto> res = attemptService.getAttemptsByTestId(testId);

        // then
        verify(attemptRepository).findByTestId(testId);
        assertThat(res, hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenGetAttemptByUserId_thenReturnAttempts() {
        // given
        String userId = "1234-qwer";

        List<AttemptResult> attempts = List.of(
                AttemptResult.builder().id("1234").userId(userId).score(10).build(),
                AttemptResult.builder().id("4321").userId(userId).score(10).build()
        );

        // when
        when(attemptRepository.findByUserId(userId)).thenReturn(attempts);
        List<AttemptResultDto> res = attemptService.getAttemptsByUserId(userId);

        // then
        verify(attemptRepository).findByUserId(userId);
        assertThat(res, hasSize(2));
    }

    private TestDto buildTest(String id) {
        QuestionDto question1 = QuestionDto.builder()
                .id(1L)
                .question("What is the correct answer to question 1?")
                .options(Set.of(
                        OptionDto.builder().id(1L).option("a").build(),
                        OptionDto.builder().id(2L).option("b").correct(true).build()
                ))
                .build();

        QuestionDto question2 = QuestionDto.builder()
                .id(2L)
                .question("What is the correct answer to question 2?")
                .options(Set.of(
                        OptionDto.builder().id(3L).option("a").build(),
                        OptionDto.builder().id(4L).option("b").correct(true).build(),
                        OptionDto.builder().id(5L).option("c").correct(true).build()
                ))
                .build();

        return TestDto.builder()
                .id(id)
                .name("TestDto")
                .questions(Set.of(question1, question2))
                .build();
    }
}