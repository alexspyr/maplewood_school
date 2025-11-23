package com.maplewood.repository;

import com.maplewood.entity.StudentCourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCourseHistoryRepository extends JpaRepository<StudentCourseHistory, Integer> {
    List<StudentCourseHistory> findByStudentId(Integer studentId);
    
    @Query("SELECT h FROM StudentCourseHistory h WHERE h.studentId = :studentId AND h.status = 'passed'")
    List<StudentCourseHistory> findPassedCoursesByStudentId(@Param("studentId") Integer studentId);
    
    @Query("SELECT COUNT(h) > 0 FROM StudentCourseHistory h WHERE h.studentId = :studentId AND h.courseId = :courseId AND h.status = 'passed'")
    boolean existsByStudentIdAndCourseIdAndStatusPassed(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);
}

