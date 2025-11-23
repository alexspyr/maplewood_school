package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AcademicProgressDto {
    private Integer creditsEarned;
    private Integer creditsRequired;
    private Integer coreCoursesCompleted;
    private Integer coreCoursesRequired;
    private BigDecimal gpa;
    private String graduationStatus;
    private Integer projectedGraduationYear;
}

