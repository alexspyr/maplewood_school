package com.maplewood.service;

import com.maplewood.dto.*;
import com.maplewood.entity.*;
import com.maplewood.repository.*;
import com.maplewood.util.DtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for student course planning and enrollment.
 * Uses extracted components (DtoMapper, ConflictValidator, ProgressCalculator) to follow SOLID principles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentPlanningService {

    private final StudentRepository studentRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final StudentCourseEnrollmentRepository enrollmentRepository;
    private final StudentCourseHistoryRepository historyRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final CourseSectionMeetingRepository meetingRepository;
    private final DtoMapper dtoMapper;
    private final ConflictValidator conflictValidator;
    private final ProgressCalculator progressCalculator;

    private static final int MAX_COURSES_PER_SEMESTER = 5;

    public StudentPlanResponse getStudentPlan(Integer studentId, Integer semesterId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new IllegalArgumentException("Semester not found: " + semesterId));

        List<CourseSection> allSections = courseSectionRepository.findBySemesterId(semesterId);
        List<StudentCourseEnrollment> currentEnrollments = enrollmentRepository
                .findByStudentIdAndSemesterId(studentId, semesterId);
        Set<Integer> enrolledSectionIds = currentEnrollments.stream()
                .map(StudentCourseEnrollment::getCourseSectionId)
                .collect(Collectors.toSet());

        List<StudentCourseHistory> passedCourses = historyRepository.findPassedCoursesByStudentId(studentId);
        Set<Integer> passedCourseIds = passedCourses.stream()
                .map(StudentCourseHistory::getCourseId)
                .collect(Collectors.toSet());

        List<CourseSectionMeeting> enrolledMeetings = getEnrolledMeetings(currentEnrollments);

        List<AvailableSectionDto> availableSections = allSections.stream()
                .filter(section -> !enrolledSectionIds.contains(section.getId()))
                .map(section -> buildAvailableSectionDto(section, passedCourseIds, enrolledMeetings))
                .collect(Collectors.toList());

        List<EnrolledSectionDto> enrolledSections = currentEnrollments.stream()
                .map(this::buildEnrolledSectionDto)
                .collect(Collectors.toList());

        AcademicProgressDto progress = progressCalculator.calculateProgress(student, passedCourses);

        StudentPlanResponse response = new StudentPlanResponse();
        response.setStudent(dtoMapper.toStudentDto(student));
        response.setSemesterId(semester.getId());
        response.setSemesterName(semester.getName() + " " + semester.getYear());
        response.setAvailableSections(availableSections);
        response.setEnrolledSections(enrolledSections);
        response.setProgress(progress);

        return response;
    }

    private List<CourseSectionMeeting> getEnrolledMeetings(List<StudentCourseEnrollment> enrollments) {
        List<CourseSectionMeeting> meetings = new ArrayList<>();
        for (StudentCourseEnrollment enrollment : enrollments) {
            meetings.addAll(meetingRepository.findByCourseSectionId(enrollment.getCourseSectionId()));
        }
        return meetings;
    }

    private AvailableSectionDto buildAvailableSectionDto(CourseSection section, 
                                                         Set<Integer> passedCourseIds,
                                                         List<CourseSectionMeeting> enrolledMeetings) {
        AvailableSectionDto dto = new AvailableSectionDto();
        dto.setId(section.getId());
        
        Course course = courseRepository.findById(section.getCourseId()).orElse(null);
        if (course != null) {
            dto.setCourse(dtoMapper.toCourseDto(course));
            dto.setPrerequisitesMet(course.getPrerequisiteId() == null || 
                                   passedCourseIds.contains(course.getPrerequisiteId()));
        }
        
        dto.setTeacher(dtoMapper.toTeacherDto(section.getTeacherId()));
        dto.setClassroom(dtoMapper.toClassroomDto(section.getClassroomId()));
        dto.setMeetings(dtoMapper.toMeetingDtos(section.getId()));

        List<CourseSectionMeeting> sectionMeetings = section.getMeetings();
        boolean hasConflict = conflictValidator.hasTimeConflict(sectionMeetings, enrolledMeetings);
        dto.setHasTimeConflict(hasConflict);
        if (hasConflict) {
            dto.setConflictReason("Time conflict with enrolled courses");
        }

        Long enrolledCount = enrollmentRepository.countByCourseSectionId(section.getId());
        dto.setEnrolledCount(enrolledCount);
        dto.setRemainingCapacity(section.getCapacity() - enrolledCount.intValue());

        return dto;
    }

    private EnrolledSectionDto buildEnrolledSectionDto(StudentCourseEnrollment enrollment) {
        EnrolledSectionDto dto = new EnrolledSectionDto();
        dto.setId(enrollment.getCourseSectionId());
        
        CourseSection section = courseSectionRepository.findById(enrollment.getCourseSectionId()).orElse(null);
        if (section != null) {
            dto.setCourse(dtoMapper.toCourseDto(section.getCourseId()));
            dto.setTeacher(dtoMapper.toTeacherDto(section.getTeacherId()));
            dto.setClassroom(dtoMapper.toClassroomDto(section.getClassroomId()));
            dto.setMeetings(dtoMapper.toMeetingDtos(section.getId()));
        }
        
        return dto;
    }

    @Transactional
    public EnrollResponse enrollStudent(Integer studentId, EnrollRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new IllegalArgumentException("Semester not found"));

        List<String> errors = new ArrayList<>();
        
        List<StudentCourseEnrollment> currentEnrollments = enrollmentRepository
                .findByStudentIdAndSemesterId(studentId, request.getSemesterId());
        if (currentEnrollments.size() + request.getCourseSectionIds().size() > MAX_COURSES_PER_SEMESTER) {
            errors.add(String.format("Cannot enroll in more than %d courses per semester", MAX_COURSES_PER_SEMESTER));
        }

        List<CourseSection> sectionsToEnroll = courseSectionRepository.findAllById(request.getCourseSectionIds());
        if (sectionsToEnroll.size() != request.getCourseSectionIds().size()) {
            errors.add("One or more course sections not found");
        }

        List<StudentCourseHistory> passedCourses = historyRepository.findPassedCoursesByStudentId(studentId);
        Set<Integer> passedCourseIds = passedCourses.stream()
                .map(StudentCourseHistory::getCourseId)
                .collect(Collectors.toSet());

        List<CourseSectionMeeting> existingMeetings = getEnrolledMeetings(currentEnrollments);
        List<CourseSectionMeeting> newMeetings = new ArrayList<>();

        for (CourseSection section : sectionsToEnroll) {
            Course course = courseRepository.findById(section.getCourseId()).orElse(null);
            
            if (course != null && course.getPrerequisiteId() != null) {
                if (!passedCourseIds.contains(course.getPrerequisiteId())) {
                    errors.add(String.format("Missing prerequisite for course %s", course.getCode()));
                }
            }

            Long enrolledCount = enrollmentRepository.countByCourseSectionId(section.getId());
            if (enrolledCount >= section.getCapacity()) {
                errors.add(String.format("Course section %d is full", section.getId()));
            }

            List<CourseSectionMeeting> sectionMeetings = section.getMeetings();
            if (conflictValidator.hasTimeConflict(sectionMeetings, existingMeetings)) {
                errors.add(String.format("Time conflict with course section %d", section.getId()));
            }

            if (conflictValidator.hasTimeConflict(sectionMeetings, newMeetings)) {
                errors.add("Time conflict between selected course sections");
            }

            newMeetings.addAll(sectionMeetings);
        }

        if (!errors.isEmpty()) {
            EnrollResponse response = new EnrollResponse();
            response.setSuccess(false);
            response.setMessage("Enrollment validation failed");
            response.setErrors(errors);
            return response;
        }

        List<StudentCourseEnrollment> enrollments = new ArrayList<>();
        for (CourseSection section : sectionsToEnroll) {
            StudentCourseEnrollment enrollment = new StudentCourseEnrollment();
            enrollment.setStudentId(studentId);
            enrollment.setCourseSectionId(section.getId());
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollments.add(enrollment);
        }

        enrollmentRepository.saveAll(enrollments);

        EnrollResponse response = new EnrollResponse();
        response.setSuccess(true);
        response.setMessage("Successfully enrolled in " + enrollments.size() + " course(s)");
        response.setEnrolledSectionIds(request.getCourseSectionIds());
        return response;
    }

    public AcademicProgressDto getStudentProgress(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        
        List<StudentCourseHistory> passedCourses = historyRepository.findPassedCoursesByStudentId(studentId);
        return progressCalculator.calculateProgress(student, passedCourses);
    }
}
