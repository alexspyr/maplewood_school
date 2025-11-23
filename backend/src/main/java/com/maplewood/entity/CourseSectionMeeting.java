package com.maplewood.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "course_section_meetings")
@Getter
@Setter
public class CourseSectionMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "course_section_id", nullable = false)
    private Integer courseSectionId;

    @Column(name = "day_of_week", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false, length = 5)
    private String startTime; // Format: "HH:mm"

    @Column(name = "end_time", nullable = false, length = 5)
    private String endTime; // Format: "HH:mm"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_section_id", insertable = false, updatable = false)
    private CourseSection courseSection;

    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    }
}

