package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleSummary {
    private Integer totalSections;
    private Integer totalCourses;
    private Integer unassignedCourses;
    private String message;
}

