package com.example.subjectservice.service;

import com.example.subjectservice.payload.SubjectDto;
import com.example.subjectservice.payload.auth.UserDto;

import java.util.List;

/**
 * Subject service
 */
public interface SubjectService {
    /**
     * Save given subject
     *
     * @param req  subject dto
     * @param user authenticated user
     * @return saved subject
     */
    SubjectDto saveSubject(SubjectDto req, UserDto user);

    /**
     * Update subject with given id
     *
     * @param subjectId id of the subject
     * @param req       subject dto
     * @param user      authenticated user
     * @return updated subject
     */
    SubjectDto updateSubject(String subjectId, SubjectDto req, UserDto user);

    /**
     * Delete subject with given id
     *
     * @param subjectId id of the subject
     * @param user      authenticated user
     */
    void deleteSubject(String subjectId, UserDto user);

    /**
     * Get subject with given id
     *
     * @param subjectId id of the subject
     * @return fetched subject
     */
    SubjectDto getSubjectById(String subjectId);

    /**
     * Get all subjects
     *
     * @return list of the fetched subjects
     */
    List<SubjectDto> getAllSubjects();

    /**
     * Get subjects by id of the educator
     *
     * @param educatorId id of the educator
     * @return list of the subjects of given educator
     */
    List<SubjectDto> getSubjectsByEducatorId(String educatorId);
}
