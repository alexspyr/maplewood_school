package com.maplewood.controller;

import com.maplewood.dto.*;
import com.maplewood.service.StudentPlanningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentPlanningController {

    private final StudentPlanningService planningService;

    @GetMapping("/{studentId}/plan")
    public ResponseEntity<StudentPlanResponse> getStudentPlan(
            @PathVariable Integer studentId,
            @RequestParam Integer semesterId) {
        StudentPlanResponse response = planningService.getStudentPlan(studentId, semesterId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{studentId}/enroll")
    public ResponseEntity<EnrollResponse> enrollStudent(
            @PathVariable Integer studentId,
            @Valid @RequestBody EnrollRequest request) {
        EnrollResponse response = planningService.enrollStudent(studentId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{studentId}/progress")
    public ResponseEntity<AcademicProgressDto> getStudentProgress(@PathVariable Integer studentId) {
        AcademicProgressDto progress = planningService.getStudentProgress(studentId);
        return ResponseEntity.ok(progress);
    }
}

