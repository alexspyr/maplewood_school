package com.maplewood.repository;

import com.maplewood.entity.CourseSectionMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionMeetingRepository extends JpaRepository<CourseSectionMeeting, Integer> {
    List<CourseSectionMeeting> findByCourseSectionId(Integer courseSectionId);
    
    @Query("SELECT m FROM CourseSectionMeeting m WHERE m.courseSectionId IN :sectionIds")
    List<CourseSectionMeeting> findByCourseSectionIdIn(@Param("sectionIds") List<Integer> sectionIds);
}

