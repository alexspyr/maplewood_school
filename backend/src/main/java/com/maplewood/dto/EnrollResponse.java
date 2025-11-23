package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnrollResponse {
    private Boolean success;
    private String message;
    private List<String> errors;
    private List<Integer> enrolledSectionIds;
}

