package com.example.subjectservice;

import com.example.subjectservice.exception.ForbiddenException;
import com.example.subjectservice.exception.ResourceNotFoundException;
import com.example.subjectservice.model.Subject;
import com.example.subjectservice.payload.SubjectDto;
import com.example.subjectservice.payload.auth.UserDto;
import com.example.subjectservice.payload.auth.UserRole;
import com.example.subjectservice.repository.SubjectRepository;
import com.example.subjectservice.service.impl.SubjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceImplTest {
    @Mock
    SubjectRepository subjectRepository;
    @Spy
    ModelMapper mapper;

    @InjectMocks
    SubjectServiceImpl subjectService;

    @Captor
    ArgumentCaptor<Subject> subjectCaptor;

    @Test
    void whenSaveSubject_givenValidRequest_thenSaveSubject() {
        // given
        SubjectDto subjectDto = SubjectDto.builder().name("Test subject").description("Test subject description").build();

        UserDto user = UserDto.builder().id("1234-qwer").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        // when
        when(subjectRepository.save(Mockito.any(Subject.class))).then(AdditionalAnswers.returnsFirstArg());

        SubjectDto res = subjectService.saveSubject(subjectDto, user);

        // then
        verify(subjectRepository).save(subjectCaptor.capture());

        Subject subject = subjectCaptor.getValue();
        assertThat(subject.getEducatorId(), is(user.getId()));
        assertThat(subject.getName(), is(subjectDto.getName()));
        assertThat(subject.getDescription(), is(subjectDto.getDescription()));
        assertThat(subject.getCreatedAt(), notNullValue());

        assertThat(res.getName(), is(subjectDto.getName()));
    }

    @Test
    void whenSaveSubject_givenUserIsNotEducator_thenThrowException() {
        // given
        SubjectDto subjectDto = SubjectDto.builder().name("Test subject").description("Test subject description").build();

        UserDto user = UserDto.builder().id("1234-qwer").email("j.doe@mail.com").role(UserRole.STUDENT).build();

        // then
        assertThrows(ForbiddenException.class, () -> subjectService.saveSubject(subjectDto, user));
    }

    @Test
    void whenUpdateSubject_givenValidRequest_thenUpdateSubject() {
        // given
        String subjectId = "qwer-1234";

        SubjectDto subjectDto = SubjectDto.builder().name("Test subject").description("Test subject description").build();

        UserDto user = UserDto.builder().id("1234-qwer").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        Subject existing = Subject.builder().id(subjectId).name("Whatever").educatorId(user.getId()).build();

        // when
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(existing));
        when(subjectRepository.save(Mockito.any(Subject.class))).then(AdditionalAnswers.returnsFirstArg());

        SubjectDto res = subjectService.updateSubject(subjectId, subjectDto, user);

        // then
        verify(subjectRepository).findById(subjectId);
        verify(subjectRepository).save(subjectCaptor.capture());

        Subject subject = subjectCaptor.getValue();
        assertThat(subject.getEducatorId(), is(user.getId()));
        assertThat(subject.getName(), is(subjectDto.getName()));
        assertThat(subject.getDescription(), is(subjectDto.getDescription()));
        assertThat(subject.getUpdatedAt(), notNullValue());

        assertThat(res.getName(), is(subjectDto.getName()));
    }

    @Test
    void whenUpdateSubject_givenUserIsNotSubjectEducator_thenThrowException() {
        // given
        String subjectId = "qwer-1234";

        SubjectDto subjectDto = SubjectDto.builder().name("Test subject").description("Test subject description").build();

        UserDto user = UserDto.builder().id("1234-qwer").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        UserDto educator = UserDto.builder().id("4321-rewq").email("jane.doe@mail.com").role(UserRole.EDUCATOR).build();

        Subject existing = Subject.builder().id(subjectId).name("Whatever").educatorId(educator.getId()).build();

        // when
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(existing));

        // then
        assertThrows(ForbiddenException.class, () -> subjectService.updateSubject(subjectId, subjectDto, user));
        verify(subjectRepository).findById(subjectId);
    }


    @Test
    void whenDeleteSubject_givenValidRequest_thenDeleteSubject() {
        // given
        String subjectId = "qwer-1234";

        UserDto user = UserDto.builder().id("1234-qwer").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        Subject existing = Subject.builder().id(subjectId).name("Whatever").educatorId(user.getId()).build();

        // when
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(existing));

        subjectService.deleteSubject(subjectId, user);

        // then
        verify(subjectRepository).findById(subjectId);
        verify(subjectRepository).delete(existing);
    }

    @Test
    void whenDeleteSubject_givenUserIsNotSubjectEducator_thenThrowException() {
        // given
        String subjectId = "qwer-1234";

        UserDto user = UserDto.builder().id("1234-qwer").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        UserDto educator = UserDto.builder().id("4321-rewq").email("jane.doe@mail.com").role(UserRole.EDUCATOR).build();

        Subject existing = Subject.builder().id(subjectId).name("Whatever").educatorId(educator.getId()).build();

        // when
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(existing));

        // then
        assertThrows(ForbiddenException.class, () -> subjectService.deleteSubject(subjectId, user));
        verify(subjectRepository).findById(subjectId);
    }

    @Test
    void whenGetSubjectById_givenSubjectExists_thenReturnSubject() {
        // given
        String subjectId = "qwer-1234";

        Subject subject = Subject.builder().id(subjectId).name("Whatever").build();

        // when
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        SubjectDto res = subjectService.getSubjectById(subjectId);

        // then
        verify(subjectRepository).findById(subjectId);
        assertThat(res.getId(), is(subjectId));
        assertThat(res.getName(), is(subject.getName()));
    }

    @Test
    void whenGetSubjectById_givenSubjectDoesntExist_thenThrowException() {
        // given
        String subjectId = "qwer-1234";

        // when
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> subjectService.getSubjectById(subjectId));
        verify(subjectRepository).findById(subjectId);
    }

    @Test
    void whenGetAllSubjects_thenReturnAllSubjects() {
        // given
        List<Subject> subjects = List.of(Subject.builder().id("1234-qwer").name("Test1").build(), Subject.builder().id("qwer-1234").name("Test2").build());

        // when
        when(subjectRepository.findAll()).thenReturn(subjects);
        List<SubjectDto> res = subjectService.getAllSubjects();

        // then
        verify(subjectRepository).findAll();
        assertThat(res, hasSize(subjects.size()));
    }

    @Test
    void whenGetSubjectsByEducatorId_givenEducatorExists_thenReturnSubjects() {
        // given
        String educatorId = "4321-rewq";

        List<Subject> subjects = List.of(Subject.builder().id("1234-qwer").name("Test1").build(), Subject.builder().id("qwer-1234").name("Test2").build());

        // when
        when(subjectRepository.findAllByEducatorId(educatorId)).thenReturn(subjects);

        List<SubjectDto> res = subjectService.getSubjectsByEducatorId(educatorId);

        // then
        verify(subjectRepository).findAllByEducatorId(educatorId);

        assertThat(res, hasSize(subjects.size()));
    }
}