package com.maplewood.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateScheduleRequest {
    @NotNull(message = "Semester ID is required")
    private Integer semesterId;
}

