package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentPlanResponse {
    private StudentDto student;
    private Integer semesterId;
    private String semesterName;
    private List<AvailableSectionDto> availableSections;
    private List<EnrolledSectionDto> enrolledSections;
    private AcademicProgressDto progress;
}

