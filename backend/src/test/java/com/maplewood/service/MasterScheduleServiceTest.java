package com.maplewood.service;

import com.maplewood.entity.*;
import com.maplewood.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MasterScheduleServiceTest {

    @Autowired
    private MasterScheduleService scheduleService;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Test
    public void testGenerateSchedule() {
        // Find an active semester
        Semester semester = semesterRepository.findAll().stream()
                .filter(s -> s.getIsActive() != null && s.getIsActive())
                .findFirst()
                .orElse(semesterRepository.findAll().get(0));

        assertNotNull(semester, "Should have at least one semester");

        // Generate schedule
        var response = scheduleService.generateSchedule(semester.getId());

        assertNotNull(response);
        assertNotNull(response.getSections());
        assertNotNull(response.getSummary());
        
        // Verify sections were created
        var sections = courseSectionRepository.findBySemesterId(semester.getId());
        assertFalse(sections.isEmpty(), "Should create at least some sections");
    }
}

