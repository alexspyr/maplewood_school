package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SemesterDto {
    private Integer id;
    private String name;
    private Integer year;
    private Integer orderInYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}

