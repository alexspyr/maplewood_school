package com.maplewood.service;

import com.maplewood.entity.*;
import com.maplewood.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleGeneratorTest {

    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private CourseSectionRepository courseSectionRepository;
    @Mock
    private CourseSectionMeetingRepository meetingRepository;
    @Mock
    private EntityManager entityManager;

    private ScheduleGenerator scheduleGenerator;

    @BeforeEach
    void setUp() {
        scheduleGenerator = new ScheduleGenerator(
                teacherRepository, classroomRepository, specializationRepository,
                courseSectionRepository, meetingRepository, entityManager
        );
    }

    @Test
    void testCalculateSectionsNeeded_CoreCourse() {
        Course course = new Course();
        course.setCourseType("core");
        course.setHoursPerWeek(4);

        int sections = scheduleGenerator.calculateSectionsNeeded(course);
        assertEquals(2, sections); // 4 hours / 2 = 2 sections
    }

    @Test
    void testCalculateSectionsNeeded_ElectiveCourse() {
        Course course = new Course();
        course.setCourseType("elective");
        course.setHoursPerWeek(3);

        int sections = scheduleGenerator.calculateSectionsNeeded(course);
        assertEquals(1, sections); // 3 hours / 3 = 1 section
    }

    @Test
    void testCalculateSectionsNeeded_MinimumOne() {
        Course course = new Course();
        course.setCourseType("elective");
        course.setHoursPerWeek(1);

        int sections = scheduleGenerator.calculateSectionsNeeded(course);
        assertEquals(1, sections); // Minimum 1 section
    }
}

