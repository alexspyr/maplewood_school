package com.maplewood.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingDto {
    private Integer id;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
}

