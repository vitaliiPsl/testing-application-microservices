package com.example.testservice.service.impl;

import com.example.testservice.client.SubjectClient;
import com.example.testservice.exception.ForbiddenException;
import com.example.testservice.exception.ResourceNotFoundException;
import com.example.testservice.model.Question;
import com.example.testservice.model.Test;
import com.example.testservice.payload.SubjectDto;
import com.example.testservice.payload.auth.UserDto;
import com.example.testservice.payload.auth.UserRole;
import com.example.testservice.payload.test.OptionDto;
import com.example.testservice.payload.test.QuestionDto;
import com.example.testservice.payload.test.TestDto;
import com.example.testservice.repository.TestRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {
    @Mock
    TestRepository testRepository;
    @Mock
    SubjectClient subjectClient;
    @Spy
    ModelMapper mapper;

    @InjectMocks
    TestServiceImpl testService;

    @Captor
    ArgumentCaptor<Test> testCaptor;

    @org.junit.jupiter.api.Test
    void whenSaveTest_givenValidRequest_thenSaveTest() {
        // given
        String subjectId = "1234-qwer";

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        SubjectDto subject = SubjectDto.builder().id(subjectId).educatorId(user.getId()).build();

        QuestionDto question1 = QuestionDto.builder()
                .question("What is the correct answer to question 1?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").correct(true).build()
                ))
                .build();

        QuestionDto question2 = QuestionDto.builder()
                .question("What is the correct answer to question 2?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").correct(true).build()
                ))
                .build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .subjectId(subjectId)
                .questions(Set.of(question1, question2))
                .build();

        // when
        when(subjectClient.getSubjectById(subjectId)).thenReturn(subject);
        when(testRepository.save(any(Test.class))).then(AdditionalAnswers.returnsFirstArg());

        TestDto res = testService.saveTest(testDto, user);

        // then
        verify(subjectClient).getSubjectById(subjectId);
        verify(testRepository).save(testCaptor.capture());

        assertThat(res.getName(), is(testDto.getName()));

        Test test = testCaptor.getValue();
        assertThat(test.getName(), is(testDto.getName()));
        assertThat(test.getSubjectId(), is(subject.getId()));
        assertThat(test.getEducatorId(), is(subject.getEducatorId()));
        assertThat(test.getCreatedAt(), is(notNullValue()));

        Set<Question> questions = test.getQuestions();
        assertThat(questions, hasSize(2));
        assertThat(List.copyOf(questions).get(0).getOptions(), hasSize(2));
        assertThat(List.copyOf(questions).get(0).getCorrectOptions(), hasSize(1));
        assertThat(List.copyOf(questions).get(1).getOptions(), hasSize(2));
        assertThat(List.copyOf(questions).get(1).getCorrectOptions(), hasSize(1));
    }

    @org.junit.jupiter.api.Test
    void whenSaveTest_givenInvalidNumberOfCorrectOptions_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        SubjectDto subject = SubjectDto.builder().id(subjectId).educatorId(user.getId()).build();

        QuestionDto question1 = QuestionDto.builder()
                .question("What is the correct answer to question 1?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").correct(true).build()
                ))
                .build();

        QuestionDto question2 = QuestionDto.builder()
                .question("What is the correct answer to question 2?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").build()
                ))
                .build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .subjectId(subjectId)
                .questions(Set.of(question1, question2))
                .build();

        // when
        when(subjectClient.getSubjectById(subjectId)).thenReturn(subject);

        // then
        assertThrows(IllegalStateException.class, () -> testService.saveTest(testDto, user));
        verify(subjectClient).getSubjectById(subjectId);
    }

    @org.junit.jupiter.api.Test
    void whenSaveTest_givenInvalidNumberOptions_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        SubjectDto subject = SubjectDto.builder().id(subjectId).educatorId(user.getId()).build();

        QuestionDto question1 = QuestionDto.builder()
                .question("What is the correct answer to question 1?")
                .options(Set.of(
                        OptionDto.builder().option("a").build()
                ))
                .build();

        QuestionDto question2 = QuestionDto.builder()
                .question("What is the correct answer to question 2?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").build()
                ))
                .build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .subjectId(subjectId)
                .questions(Set.of(question1, question2))
                .build();

        // when
        when(subjectClient.getSubjectById(subjectId)).thenReturn(subject);

        // then
        assertThrows(IllegalStateException.class, () -> testService.saveTest(testDto, user));
        verify(subjectClient).getSubjectById(subjectId);
    }


    @org.junit.jupiter.api.Test
    void whenSaveTest_givenUserIsNotEducatorOfGivenSubject_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        SubjectDto subject = SubjectDto.builder().id(subjectId).educatorId("qwerasdf").build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .subjectId(subjectId)
                .build();

        // when
        when(subjectClient.getSubjectById(subjectId)).thenReturn(subject);

        // then
        assertThrows(IllegalStateException.class, () -> testService.saveTest(testDto, user));
        verify(subjectClient).getSubjectById(subjectId);
    }

    @org.junit.jupiter.api.Test
    void whenSaveTest_givenSubjectDoesntExist_thenThrowException() {
        // given
        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        String subjectId = "1234-qwer";
        TestDto testDto = TestDto.builder()
                .name("Test")
                .subjectId(subjectId)
                .build();

        // when
        when(subjectClient.getSubjectById(subjectId)).thenThrow(ResourceNotFoundException.class);

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.saveTest(testDto, user));
        verify(subjectClient).getSubjectById(subjectId);
    }

    // UPDATE
    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenValidRequest_thenUpdateTest() {
        // given
        String subjectId = "1234-qwer";

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        QuestionDto question1 = QuestionDto.builder()
                .question("What is the correct answer to question 1?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").correct(true).build()
                ))
                .build();

        QuestionDto question2 = QuestionDto.builder()
                .question("What is the correct answer to question 2?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").correct(true).build()
                ))
                .build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .subjectId(subjectId)
                .questions(Set.of(question1, question2))
                .build();

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).educatorId(user.getId()).name("First test").build();

        // when
        when(testRepository.findById(testId)).thenReturn(Optional.of(test));
        when(testRepository.save(any(Test.class))).then(AdditionalAnswers.returnsFirstArg());

        TestDto res = testService.updateTest(testId, testDto, user);

        // then
        verify(testRepository).findById(testId);
        verify(testRepository).save(testCaptor.capture());

        assertThat(res.getName(), is(testDto.getName()));

        Test capturedTest = testCaptor.getValue();
        assertThat(capturedTest.getId(), is(testId));
        assertThat(capturedTest.getName(), is(testDto.getName()));
        assertThat(capturedTest.getEducatorId(), is(user.getId()));
        assertThat(capturedTest.getUpdatedAt(), is(notNullValue()));

        Set<Question> questions = capturedTest.getQuestions();
        assertThat(questions, hasSize(2));
        assertThat(List.copyOf(questions).get(0).getOptions(), hasSize(2));
        assertThat(List.copyOf(questions).get(0).getCorrectOptions(), hasSize(1));
        assertThat(List.copyOf(questions).get(1).getOptions(), hasSize(2));
        assertThat(List.copyOf(questions).get(1).getCorrectOptions(), hasSize(1));
    }

    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenInvalidNumberOfCorrectOptions_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        QuestionDto question1 = QuestionDto.builder()
                .question("What is the correct answer to question 1?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").correct(true).build()
                ))
                .build();

        QuestionDto question2 = QuestionDto.builder()
                .question("What is the correct answer to question 2?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").build()
                ))
                .build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .subjectId(subjectId)
                .questions(Set.of(question1, question2))
                .build();

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).educatorId(user.getId()).name("First test").build();

        // when
        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        // then
        assertThrows(IllegalStateException.class, () -> testService.updateTest(testId, testDto, user));
        verify(testRepository).findById(testId);
    }

    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenInvalidNumberOfOptions_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        QuestionDto question1 = QuestionDto.builder()
                .question("What is the correct answer to question 1?")
                .options(Set.of(
                        OptionDto.builder().option("a").build()
                ))
                .build();

        QuestionDto question2 = QuestionDto.builder()
                .question("What is the correct answer to question 2?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").build()
                ))
                .build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .subjectId(subjectId)
                .questions(Set.of(question1, question2))
                .build();

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).educatorId(user.getId()).name("First test").build();

        // when
        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        // then
        assertThrows(IllegalStateException.class, () -> testService.updateTest(testId, testDto, user));
        verify(testRepository).findById(testId);
    }

    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenTestDoesntExist_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .subjectId(subjectId)
                .build();

        String testId = "qwer-1234";

        // when
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.updateTest(testId, testDto, user));
        verify(testRepository).findById(testId);
    }

    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenUserIsNotEducatorOfGivenSubject_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        UserDto educator = UserDto.builder().id("rewq-4321").email("jane.doe@mail.com").role(UserRole.EDUCATOR).build();

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).educatorId(educator.getId()).build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .subjectId(subjectId)
                .build();

        // when
        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        // then
        assertThrows(ForbiddenException.class, () -> testService.updateTest(testId, testDto, user));
        verify(testRepository).findById(testId);
    }

    // DELETE
    @org.junit.jupiter.api.Test
    void whenDeleteTest_givenValidRequest_thenDeleteTest() {
        // given
        String subjectId = "1234-qwer";

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).educatorId(user.getId()).name("First test").build();

        // when
        when(testRepository.findById(testId)).thenReturn(Optional.of(test));
        testService.deleteTest(testId, user);

        // then
        verify(testRepository).findById(testId);
        verify(testRepository).delete(test);
    }

    @org.junit.jupiter.api.Test
    void whenDeleteTest_givenTestDoesntExist_thenThrowException() {
        // given
        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        String testId = "qwer-1234";

        // when
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.deleteTest(testId, user));
        verify(testRepository).findById(testId);
    }

    @org.junit.jupiter.api.Test
    void whenDeleteTest_givenUserIsNotEducatorOfTheSubject_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        UserDto user = UserDto.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        UserDto educator = UserDto.builder().id("rewq-4321").email("jane.doe@mail.com").role(UserRole.EDUCATOR).build();

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).educatorId(educator.getId()).name("First test").build();

        // when
        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        // then
        assertThrows(ForbiddenException.class, () -> testService.deleteTest(testId, user));
        verify(testRepository).findById(testId);
    }

    // FETCH
    @org.junit.jupiter.api.Test
    void whenGetTestById_givenTestExist_thenReturnTest() {
        // given
        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).name("First test").build();

        // when
        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        TestDto res = testService.getTestById(testId);

        // then
        verify(testRepository).findById(testId);

        assertThat(res.getId(), is(test.getId()));
        assertThat(res.getName(), is(test.getName()));
    }

    @org.junit.jupiter.api.Test
    void whenGetTestById_givenTestDoesntExist_thenThrowException() {
        // given
        String testId = "qwer-1234";

        // when
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.getTestById(testId));
        verify(testRepository).findById(testId);
    }

    @org.junit.jupiter.api.Test
    void whenGetTestsBySubjectId_givenTestsWithGivenSubjectIdExist_thenReturnTests() {
        // given
        String subjectId = "1234-qwer";

        List<Test> tests = List.of(
                Test.builder().id("1234").subjectId(subjectId).name("First test").build(),
                Test.builder().id("4321").subjectId(subjectId).name("Second test").build()
        );

        // when
        when(testRepository.findBySubjectId(subjectId)).thenReturn(tests);

        List<TestDto> res = testService.getTestsBySubjectId(subjectId);

        // then
        verify(testRepository).findBySubjectId(subjectId);

        assertThat(res, hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenGetTestsBySubjectId_givenTestsWithGivenSubjectIdDoesntExist_thenReturnEmptyList() {
        // given
        String subjectId = "1234-qwer";

        List<Test> tests = List.of();

        // when
        when(testRepository.findBySubjectId(subjectId)).thenReturn(tests);

        List<TestDto> res = testService.getTestsBySubjectId(subjectId);

        // then
        verify(testRepository).findBySubjectId(subjectId);

        assertThat(res, hasSize(0));
    }
}