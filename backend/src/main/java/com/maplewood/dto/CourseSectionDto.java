package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseSectionDto {
    private Integer id;
    private CourseDto course;
    private TeacherDto teacher;
    private ClassroomDto classroom;
    private List<MeetingDto> meetings;
    private Integer capacity;
    private Long enrolledCount;
    private Integer remainingCapacity;
}

