package com.maplewood.service;

import com.maplewood.dto.AcademicProgressDto;
import com.maplewood.entity.Course;
import com.maplewood.entity.Student;
import com.maplewood.entity.StudentCourseHistory;
import com.maplewood.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Extracted academic progress calculation logic.
 * Follows Single Responsibility Principle - only calculates student progress.
 */
@Component
@RequiredArgsConstructor
public class ProgressCalculator {

    private final CourseRepository courseRepository;
    private static final int REQUIRED_CREDITS = 30;
    private static final int REQUIRED_CORE_COURSES = 20;

    public AcademicProgressDto calculateProgress(Student student, List<StudentCourseHistory> passedCourses) {
        AcademicProgressDto progress = new AcademicProgressDto();
        
        int creditsEarned = 0;
        int coreCoursesCompleted = 0;
        
        for (StudentCourseHistory history : passedCourses) {
            Course course = courseRepository.findById(history.getCourseId()).orElse(null);
            if (course != null) {
                creditsEarned += course.getCredits().intValue();
                if ("core".equals(course.getCourseType())) {
                    coreCoursesCompleted++;
                }
            }
        }
        
        progress.setCreditsEarned(creditsEarned);
        progress.setCreditsRequired(REQUIRED_CREDITS);
        progress.setCoreCoursesCompleted(coreCoursesCompleted);
        progress.setCoreCoursesRequired(REQUIRED_CORE_COURSES);
        
        // Calculate GPA (simplified: assume all passed courses = 4.0, failed = 0.0)
        List<StudentCourseHistory> allHistory = passedCourses; // In real app, get all history
        if (!allHistory.isEmpty()) {
            BigDecimal gpa = BigDecimal.valueOf(4.0 * passedCourses.size() / (double) allHistory.size())
                    .setScale(2, RoundingMode.HALF_UP);
            progress.setGpa(gpa);
        } else {
            progress.setGpa(BigDecimal.ZERO);
        }
        
        // Project graduation
        int creditsRemaining = REQUIRED_CREDITS - creditsEarned;
        int semestersRemaining = (int) Math.ceil(creditsRemaining / 15.0); // Assume 15 credits per semester
        int projectedYear = student.getEnrollmentYear() + 4 + semestersRemaining / 2;
        
        progress.setProjectedGraduationYear(projectedYear);
        
        if (creditsEarned >= REQUIRED_CREDITS && coreCoursesCompleted >= REQUIRED_CORE_COURSES) {
            progress.setGraduationStatus("Eligible for graduation");
        } else {
            progress.setGraduationStatus(String.format("On track to graduate in %d", projectedYear));
        }
        
        return progress;
    }
}

