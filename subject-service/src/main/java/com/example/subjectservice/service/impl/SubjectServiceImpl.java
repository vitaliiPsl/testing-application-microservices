package com.example.subjectservice.service.impl;

import com.example.subjectservice.exception.ForbiddenException;
import com.example.subjectservice.exception.ResourceNotFoundException;
import com.example.subjectservice.model.Subject;
import com.example.subjectservice.payload.SubjectDto;
import com.example.subjectservice.payload.auth.UserDto;
import com.example.subjectservice.payload.auth.UserRole;
import com.example.subjectservice.repository.SubjectRepository;
import com.example.subjectservice.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final ModelMapper mapper;

    @CachePut(value = "subjects", key = "#{result.id}")
    @Override
    public SubjectDto saveSubject(SubjectDto req, UserDto user) {
        log.debug("Save subject: {}", req);

        if (user.getRole() != UserRole.EDUCATOR) {
            log.error("Only educator can create subjects");
            throw new ForbiddenException("Only educator can create subjects");
        }

        // save subject
        Subject subject = createSubject(req, user);
        subject = subjectRepository.save(subject);

        return mapSubjectToSubjectDto(subject);
    }

    @CachePut(value = "subjects", key = "#{result.id}")
    @Override
    public SubjectDto updateSubject(String subjectId, SubjectDto req, UserDto user) {
        log.debug("Update subject with id {}. New data: {}", subjectId, req);

        // find subject by id
        Subject subject = getSubject(subjectId);

        // check that user is the subject educator
        if (!subject.getEducatorId().equals(user.getId())) {
            log.error("User is not the educator on given subject");
            throw new ForbiddenException("Not a subject educator");
        }

        // update properties
        subject.setName(req.getName());
        subject.setDescription(req.getDescription());
        subject.setUpdatedAt(LocalDateTime.now());

        subject = subjectRepository.save(subject);

        return mapSubjectToSubjectDto(subject);
    }

    @CacheEvict(value = "subjects", key = "#subjectId")
    @Override
    public void deleteSubject(String subjectId, UserDto user) {
        log.debug("Delete subject with id {}", subjectId);

        // find subject by id
        Subject subject = getSubject(subjectId);

        // check that user is the subject educator
        if (!subject.getEducatorId().equals(user.getId())) {
            log.error("User is not the educator on given subject");
            throw new ForbiddenException("Not a subject educator");
        }

        subjectRepository.delete(subject);
    }

    @Cacheable(value = "subjects", key = "#subjectId")
    @Override
    public SubjectDto getSubjectById(String subjectId) {
        log.debug("Get subject with id {}", subjectId);

        // find subject by id
        Subject subject = getSubject(subjectId);

        return mapSubjectToSubjectDto(subject);
    }

    @Override
    public List<SubjectDto> getAllSubjects() {
        log.debug("Get all subjects");

        return subjectRepository.findAll()
                .stream().map(this::mapSubjectToSubjectDto).collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> getSubjectsByEducatorId(String educatorId) {
        log.debug("Get subject by educator id {}", educatorId);

        return subjectRepository.findAllByEducatorId(educatorId)
                .stream().map(this::mapSubjectToSubjectDto).collect(Collectors.toList());
    }

    private Subject getSubject(String subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));
    }

    private Subject createSubject(SubjectDto dto, UserDto educator) {
        return Subject.builder()
                .educatorId(educator.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private SubjectDto mapSubjectToSubjectDto(Subject subject) {
        return mapper.map(subject, SubjectDto.class);
    }
}
