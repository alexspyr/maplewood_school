package com.maplewood.entity;

import com.maplewood.config.SqliteDateConverters;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_course_enrollments", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_section_id"}))
@Getter
@Setter
public class StudentCourseEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "course_section_id", nullable = false)
    private Integer courseSectionId;

    @Column(name = "enrolled_at", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = SqliteDateConverters.LocalDateTimeConverter.class)
    private LocalDateTime enrolledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_section_id", insertable = false, updatable = false)
    private CourseSection courseSection;
}

