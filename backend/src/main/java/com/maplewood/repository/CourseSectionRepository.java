package com.maplewood.repository;

import com.maplewood.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Integer> {
    List<CourseSection> findBySemesterId(Integer semesterId);
    
    @Query("SELECT cs FROM CourseSection cs WHERE cs.semesterId = :semesterId AND cs.teacherId = :teacherId")
    List<CourseSection> findBySemesterIdAndTeacherId(@Param("semesterId") Integer semesterId, @Param("teacherId") Integer teacherId);
    
    @Query("SELECT cs FROM CourseSection cs WHERE cs.semesterId = :semesterId AND cs.classroomId = :classroomId")
    List<CourseSection> findBySemesterIdAndClassroomId(@Param("semesterId") Integer semesterId, @Param("classroomId") Integer classroomId);
}

