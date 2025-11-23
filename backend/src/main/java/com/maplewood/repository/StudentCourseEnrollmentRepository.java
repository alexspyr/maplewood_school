package com.maplewood.repository;

import com.maplewood.entity.StudentCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCourseEnrollmentRepository extends JpaRepository<StudentCourseEnrollment, Integer> {
    List<StudentCourseEnrollment> findByStudentId(Integer studentId);
    
    @Query("SELECT e FROM StudentCourseEnrollment e WHERE e.studentId = :studentId AND e.courseSection.semesterId = :semesterId")
    List<StudentCourseEnrollment> findByStudentIdAndSemesterId(@Param("studentId") Integer studentId, @Param("semesterId") Integer semesterId);
    
    @Query("SELECT COUNT(e) FROM StudentCourseEnrollment e WHERE e.courseSectionId = :courseSectionId")
    Long countByCourseSectionId(@Param("courseSectionId") Integer courseSectionId);
}

