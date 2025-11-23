package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUsageDto {
    private Integer roomId;
    private String roomName;
    private Integer totalSections;
    private Integer totalHoursPerWeek;
    private Double utilizationPercentage;
}

