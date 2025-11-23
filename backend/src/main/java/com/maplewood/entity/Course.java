package com.maplewood.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "REAL")
    private BigDecimal credits;

    @Column(name = "hours_per_week", nullable = false)
    private Integer hoursPerWeek;

    @Column(name = "specialization_id", nullable = false)
    private Integer specializationId;

    @Column(name = "prerequisite_id")
    private Integer prerequisiteId;

    @Column(name = "course_type", nullable = false, length = 20)
    private String courseType;

    @Column(name = "grade_level_min")
    private Integer gradeLevelMin;

    @Column(name = "grade_level_max")
    private Integer gradeLevelMax;

    @Column(name = "semester_order", nullable = false)
    private Integer semesterOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", insertable = false, updatable = false)
    private Specialization specialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_id", insertable = false, updatable = false)
    private Course prerequisite;
}

