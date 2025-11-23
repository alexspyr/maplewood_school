package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnrolledSectionDto {
    private Integer id;
    private CourseDto course;
    private TeacherDto teacher;
    private ClassroomDto classroom;
    private List<MeetingDto> meetings;
}

