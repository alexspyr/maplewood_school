package com.maplewood.entity;

import com.maplewood.config.SqliteDateConverters;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "semesters")
@Getter
@Setter
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "order_in_year", nullable = false)
    private Integer orderInYear;

    @Column(name = "start_date", columnDefinition = "TEXT")
    @Convert(converter = SqliteDateConverters.LocalDateConverter.class)
    private LocalDate startDate;

    @Column(name = "end_date", columnDefinition = "TEXT")
    @Convert(converter = SqliteDateConverters.LocalDateConverter.class)
    private LocalDate endDate;

    @Column(name = "is_active")
    private Boolean isActive;
}

