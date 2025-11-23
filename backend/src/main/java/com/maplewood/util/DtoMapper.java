package com.maplewood.util;

import com.maplewood.dto.*;
import com.maplewood.entity.*;
import com.maplewood.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized DTO mapper utility to eliminate code duplication.
 * Follows Single Responsibility Principle - only responsible for entity-to-DTO conversion.
 */
@Component
@RequiredArgsConstructor
public class DtoMapper {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final CourseSectionMeetingRepository meetingRepository;

    public CourseDto toCourseDto(Course course) {
        if (course == null) return null;
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setCode(course.getCode());
        dto.setName(course.getName());
        dto.setCredits(course.getCredits());
        dto.setHoursPerWeek(course.getHoursPerWeek());
        dto.setCourseType(course.getCourseType());
        dto.setSemesterOrder(course.getSemesterOrder());
        return dto;
    }

    public CourseDto toCourseDto(Integer courseId) {
        if (courseId == null) return null;
        return courseRepository.findById(courseId)
                .map(this::toCourseDto)
                .orElse(null);
    }

    public TeacherDto toTeacherDto(Teacher teacher) {
        if (teacher == null) return null;
        TeacherDto dto = new TeacherDto();
        dto.setId(teacher.getId());
        dto.setFirstName(teacher.getFirstName());
        dto.setLastName(teacher.getLastName());
        dto.setEmail(teacher.getEmail());
        return dto;
    }

    public TeacherDto toTeacherDto(Integer teacherId) {
        if (teacherId == null) return null;
        return teacherRepository.findById(teacherId)
                .map(this::toTeacherDto)
                .orElse(null);
    }

    public ClassroomDto toClassroomDto(Classroom classroom) {
        if (classroom == null) return null;
        ClassroomDto dto = new ClassroomDto();
        dto.setId(classroom.getId());
        dto.setName(classroom.getName());
        dto.setCapacity(classroom.getCapacity());
        if (classroom.getRoomTypeId() != null) {
            roomTypeRepository.findById(classroom.getRoomTypeId())
                    .ifPresent(roomType -> dto.setRoomType(roomType.getName()));
        }
        return dto;
    }

    public ClassroomDto toClassroomDto(Integer classroomId) {
        if (classroomId == null) return null;
        return classroomRepository.findById(classroomId)
                .map(this::toClassroomDto)
                .orElse(null);
    }

    public MeetingDto toMeetingDto(CourseSectionMeeting meeting) {
        if (meeting == null) return null;
        MeetingDto dto = new MeetingDto();
        dto.setId(meeting.getId());
        dto.setDayOfWeek(meeting.getDayOfWeek().name());
        dto.setStartTime(meeting.getStartTime());
        dto.setEndTime(meeting.getEndTime());
        return dto;
    }

    public List<MeetingDto> toMeetingDtos(Integer courseSectionId) {
        if (courseSectionId == null) return List.of();
        return meetingRepository.findByCourseSectionId(courseSectionId).stream()
                .map(this::toMeetingDto)
                .collect(Collectors.toList());
    }

    public List<MeetingDto> toMeetingDtos(List<CourseSectionMeeting> meetings) {
        if (meetings == null) return List.of();
        return meetings.stream()
                .map(this::toMeetingDto)
                .collect(Collectors.toList());
    }

    public StudentDto toStudentDto(Student student) {
        if (student == null) return null;
        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setGradeLevel(student.getGradeLevel());
        dto.setEmail(student.getEmail());
        return dto;
    }

    public SemesterDto toSemesterDto(Semester semester) {
        if (semester == null) return null;
        SemesterDto dto = new SemesterDto();
        dto.setId(semester.getId());
        dto.setName(semester.getName());
        dto.setYear(semester.getYear());
        dto.setOrderInYear(semester.getOrderInYear());
        dto.setStartDate(semester.getStartDate());
        dto.setEndDate(semester.getEndDate());
        dto.setIsActive(semester.getIsActive());
        return dto;
    }
}

