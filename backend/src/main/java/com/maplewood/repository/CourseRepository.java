package com.maplewood.repository;

import com.maplewood.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findBySemesterOrder(Integer semesterOrder);
    
    @Query("SELECT c FROM Course c WHERE c.semesterOrder = :semesterOrder AND c.gradeLevelMin <= :gradeLevel AND c.gradeLevelMax >= :gradeLevel")
    List<Course> findBySemesterOrderAndGradeLevel(@Param("semesterOrder") Integer semesterOrder, @Param("gradeLevel") Integer gradeLevel);
}

