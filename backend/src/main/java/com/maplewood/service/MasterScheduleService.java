package com.maplewood.service;

import com.maplewood.dto.*;
import com.maplewood.entity.*;
import com.maplewood.repository.*;
import com.maplewood.util.DtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for master schedule generation.
 * Delegates scheduling algorithm to ScheduleGenerator (Single Responsibility).
 * Uses DtoMapper for entity-to-DTO conversion (DRY principle).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MasterScheduleService {

    private final CourseSectionRepository courseSectionRepository;
    private final CourseSectionMeetingRepository meetingRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final StudentCourseEnrollmentRepository enrollmentRepository;
    private final ScheduleGenerator scheduleGenerator;
    private final DtoMapper dtoMapper;

    @Transactional
    public ScheduleResponse generateSchedule(Integer semesterId) {
        log.info("Generating master schedule for semester ID: {}", semesterId);
        
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new IllegalArgumentException("Semester not found: " + semesterId));

        // Delete existing schedule for this semester
        List<CourseSection> existingSections = courseSectionRepository.findBySemesterId(semesterId);
        if (!existingSections.isEmpty()) {
            log.info("Deleting {} existing sections for semester {}", existingSections.size(), semesterId);
            courseSectionRepository.deleteAll(existingSections);
        }

        // Get all courses for this semester (based on semester order)
        List<Course> courses = courseRepository.findBySemesterOrder(semester.getOrderInYear());
        log.info("Found {} courses for semester order {}", courses.size(), semester.getOrderInYear());

        List<CourseSection> createdSections = new ArrayList<>();
        List<Course> unassignedCourses = new ArrayList<>();

        // Track teacher and room availability
        Map<Integer, Map<String, List<ScheduleGenerator.TimeSlot>>> teacherSchedule = new HashMap<>();
        Map<Integer, Map<String, List<ScheduleGenerator.TimeSlot>>> roomSchedule = new HashMap<>();

        // Sort courses by priority: core courses first, then by hours (more hours = more priority)
        // This helps allocate resources to important courses first
        courses.sort((c1, c2) -> {
            if ("core".equals(c1.getCourseType()) && !"core".equals(c2.getCourseType())) return -1;
            if (!"core".equals(c1.getCourseType()) && "core".equals(c2.getCourseType())) return 1;
            return Integer.compare(c2.getHoursPerWeek(), c1.getHoursPerWeek()); // More hours first
        });

        for (Course course : courses) {
            log.info("Processing course: {} ({} hours/week, type: {})", 
                    course.getCode(), course.getHoursPerWeek(), course.getCourseType());
            
            int sectionsNeeded = scheduleGenerator.calculateSectionsNeeded(course);
            int sectionsCreated = 0;
            
            for (int i = 0; i < sectionsNeeded; i++) {
                CourseSection section = scheduleGenerator.createSection(
                        course, semester, teacherSchedule, roomSchedule);
                if (section != null) {
                    createdSections.add(section);
                    sectionsCreated++;
                    log.debug("Created section {} for course {}", i + 1, course.getCode());
                } else {
                    log.warn("Could not create section {} for course {} - no available time slots", 
                            i + 1, course.getCode());
                }
            }
            
            if (sectionsCreated == 0) {
                log.warn("Course {} could not be assigned - added to unassigned list", course.getCode());
                unassignedCourses.add(course);
            } else {
                log.info("Successfully created {} sections for course {}", sectionsCreated, course.getCode());
            }
        }

        log.info("Created {} sections, {} courses unassigned", createdSections.size(), unassignedCourses.size());
        return buildScheduleResponse(semester, createdSections, unassignedCourses);
    }

    public ScheduleResponse getSchedule(Integer semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new IllegalArgumentException("Semester not found: " + semesterId));

        List<CourseSection> sections = courseSectionRepository.findBySemesterId(semesterId);
        return buildScheduleResponse(semester, sections, Collections.emptyList());
    }

    private ScheduleResponse buildScheduleResponse(Semester semester, List<CourseSection> sections,
                                                  List<Course> unassignedCourses) {
        ScheduleResponse response = new ScheduleResponse();
        response.setSemesterId(semester.getId());
        response.setSemesterName(semester.getName() + " " + semester.getYear());

        List<CourseSectionDto> sectionDtos = sections.stream()
                .map(this::toSectionDto)
                .collect(Collectors.toList());
        response.setSections(sectionDtos);

        ScheduleSummary summary = new ScheduleSummary();
        summary.setTotalSections(sections.size());
        summary.setTotalCourses((int) sections.stream().map(CourseSection::getCourseId).distinct().count());
        summary.setUnassignedCourses(unassignedCourses.size());
        summary.setMessage(String.format("Generated %d sections for %d courses. %d courses could not be assigned.",
                sections.size(), summary.getTotalCourses(), unassignedCourses.size()));
        response.setSummary(summary);

        return response;
    }

    private CourseSectionDto toSectionDto(CourseSection section) {
        CourseSectionDto dto = new CourseSectionDto();
        dto.setId(section.getId());
        dto.setCourse(dtoMapper.toCourseDto(section.getCourseId()));
        dto.setTeacher(dtoMapper.toTeacherDto(section.getTeacherId()));
        dto.setClassroom(dtoMapper.toClassroomDto(section.getClassroomId()));
        dto.setCapacity(section.getCapacity());
        dto.setMeetings(dtoMapper.toMeetingDtos(section.getId()));

        // Get enrollment count
        Long enrolledCount = enrollmentRepository.countByCourseSectionId(section.getId());
        dto.setEnrolledCount(enrolledCount);
        dto.setRemainingCapacity(section.getCapacity() - enrolledCount.intValue());

        return dto;
    }
}
