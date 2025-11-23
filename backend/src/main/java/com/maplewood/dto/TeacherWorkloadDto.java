package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TeacherWorkloadDto {
    private Integer teacherId;
    private String teacherName;
    private Integer totalHoursPerWeek;
    private Map<String, Integer> hoursPerDay; // Day -> hours
    private Integer sectionCount;
}

