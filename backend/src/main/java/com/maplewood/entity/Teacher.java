package com.maplewood.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "teachers")
@Getter
@Setter
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "specialization_id", nullable = false)
    private Integer specializationId;

    @Column(length = 100)
    private String email;

    @Column(name = "max_daily_hours")
    private Integer maxDailyHours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", insertable = false, updatable = false)
    private Specialization specialization;
}

