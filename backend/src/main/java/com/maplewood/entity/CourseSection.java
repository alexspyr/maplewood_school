package com.maplewood.entity;

import com.maplewood.config.SqliteDateConverters;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_sections")
@Getter
@Setter
public class CourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "course_id", nullable = false)
    private Integer courseId;

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Column(name = "classroom_id", nullable = false)
    private Integer classroomId;

    @Column(name = "semester_id", nullable = false)
    private Integer semesterId;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "created_at", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = SqliteDateConverters.LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", insertable = false, updatable = false)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", insertable = false, updatable = false)
    private Semester semester;

    @OneToMany(mappedBy = "courseSection", fetch = FetchType.LAZY)
    private List<CourseSectionMeeting> meetings = new ArrayList<>();

    @OneToMany(mappedBy = "courseSection", fetch = FetchType.LAZY)
    private List<StudentCourseEnrollment> enrollments = new ArrayList();
}

