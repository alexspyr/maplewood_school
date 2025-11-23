package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CourseDto {
    private Integer id;
    private String code;
    private String name;
    private BigDecimal credits;
    private Integer hoursPerWeek;
    private String courseType;
    private Integer semesterOrder;
}

