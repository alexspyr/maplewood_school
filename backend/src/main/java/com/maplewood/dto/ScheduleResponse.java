package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScheduleResponse {
    private Integer semesterId;
    private String semesterName;
    private List<CourseSectionDto> sections;
    private ScheduleSummary summary;
}

