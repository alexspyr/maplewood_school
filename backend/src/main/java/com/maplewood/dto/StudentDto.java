package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer gradeLevel;
    private String email;
}

