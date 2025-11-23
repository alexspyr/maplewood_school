package com.maplewood.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnrollRequest {
    @NotNull(message = "Semester ID is required")
    private Integer semesterId;
    
    @NotNull(message = "Course section IDs are required")
    @Size(min = 1, max = 5, message = "Must enroll in 1-5 courses")
    private List<Integer> courseSectionIds;
}

