package com.maplewood.controller;

import com.maplewood.dto.*;
import com.maplewood.entity.Semester;
import com.maplewood.repository.SemesterRepository;
import com.maplewood.service.MasterScheduleService;
import com.maplewood.util.DtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final MasterScheduleService scheduleService;
    private final SemesterRepository semesterRepository;
    private final DtoMapper dtoMapper;

    @PostMapping("/schedules/generate")
    public ResponseEntity<ScheduleResponse> generateSchedule(@Valid @RequestBody GenerateScheduleRequest request) {
        ScheduleResponse response = scheduleService.generateSchedule(request.getSemesterId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/schedules/{semesterId}")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable Integer semesterId) {
        ScheduleResponse response = scheduleService.getSchedule(semesterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teachers/workload")
    public ResponseEntity<List<TeacherWorkloadDto>> getTeacherWorkload(@RequestParam Integer semesterId) {
        // TODO: Implement teacher workload calculation
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/rooms/usage")
    public ResponseEntity<List<RoomUsageDto>> getRoomUsage(@RequestParam Integer semesterId) {
        // TODO: Implement room usage calculation
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/semesters")
    public ResponseEntity<List<SemesterDto>> getAllSemesters() {
        List<Semester> semesters = semesterRepository.findAll();
        List<SemesterDto> semesterDtos = semesters.stream()
                .map(dtoMapper::toSemesterDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(semesterDtos);
    }
}

