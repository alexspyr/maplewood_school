package com.maplewood.service;

import com.maplewood.entity.*;
import com.maplewood.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Extracted scheduling algorithm logic following Single Responsibility Principle.
 * Responsible only for the constraint satisfaction algorithm.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleGenerator {

    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final SpecializationRepository specializationRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final CourseSectionMeetingRepository meetingRepository;
    private final EntityManager entityManager;

    private static final int SCHOOL_START_HOUR = 9;
    private static final int SCHOOL_END_HOUR = 17;
    private static final int LUNCH_START_HOUR = 12;
    private static final int LUNCH_END_HOUR = 13;
    private static final int MAX_DAILY_TEACHER_HOURS = 4;
    private static final int MAX_SESSION_HOURS = 2;
    private static final int MIN_SESSION_HOURS = 1;

    /**
     * Attempts to create a course section with teacher, room, and time slot assignments.
     * Returns null if no valid assignment can be found.
     */
    public CourseSection createSection(Course course, Semester semester,
                                      Map<Integer, Map<String, List<TimeSlot>>> teacherSchedule,
                                      Map<Integer, Map<String, List<TimeSlot>>> roomSchedule) {
        List<Teacher> teachers = teacherRepository.findBySpecializationId(course.getSpecializationId());
        if (teachers.isEmpty()) {
            log.warn("No teachers found for specialization {} (course: {})", course.getSpecializationId(), course.getCode());
            return null;
        }

        Specialization specialization = specializationRepository.findById(course.getSpecializationId())
                .orElseThrow(() -> new IllegalArgumentException("Specialization not found: " + course.getSpecializationId()));
        Integer requiredRoomTypeId = specialization.getRoomTypeId();

        // If specialization doesn't have a room type assigned, use default classroom (room_type_id = 1)
        // This handles cases where the database has NULL room_type_id for specializations
        if (requiredRoomTypeId == null) {
            log.debug("Specialization {} has no room type assigned, using default classroom (room_type_id=1)", 
                    specialization.getName());
            requiredRoomTypeId = 1; // Default to general classroom
        }

        List<Classroom> availableRooms = classroomRepository.findByRoomTypeId(requiredRoomTypeId);
        if (availableRooms.isEmpty()) {
            log.warn("No rooms found for room type {} (course: {}, specialization: {})", 
                    requiredRoomTypeId, course.getCode(), specialization.getName());
            return null;
        }

        log.debug("Trying to create section for course {} ({} hours/week). {} teachers, {} rooms available", 
                course.getCode(), course.getHoursPerWeek(), teachers.size(), availableRooms.size());

        for (Teacher teacher : teachers) {
            for (Classroom room : availableRooms) {
                List<TimeSlot> timeSlots = findAvailableTimeSlots(
                        course.getHoursPerWeek(),
                        teacher.getId(),
                        room.getId(),
                        teacherSchedule,
                        roomSchedule
                );

                if (timeSlots != null && !timeSlots.isEmpty()) {
                    log.debug("Successfully allocated {} time slots for course {} with teacher {} and room {}", 
                            timeSlots.size(), course.getCode(), teacher.getId(), room.getId());
                    
                    CourseSection section = new CourseSection();
                    section.setCourseId(course.getId());
                    section.setTeacherId(teacher.getId());
                    section.setClassroomId(room.getId());
                    section.setSemesterId(semester.getId());
                    section.setCapacity(room.getCapacity());
                    section.setCreatedAt(java.time.LocalDateTime.now());
                    
                    // Save section first to get the ID
                    section = courseSectionRepository.save(section);
                    entityManager.flush(); // Ensure ID is generated for SQLite
                    
                    // Save meetings directly - don't use bidirectional relationship to avoid collection issues
                    Integer sectionId = section.getId();
                    for (TimeSlot slot : timeSlots) {
                        CourseSectionMeeting meeting = new CourseSectionMeeting();
                        meeting.setCourseSectionId(sectionId);
                        meeting.setDayOfWeek(CourseSectionMeeting.DayOfWeek.valueOf(slot.day));
                        meeting.setStartTime(slot.startTime);
                        meeting.setEndTime(slot.endTime);
                        meetingRepository.save(meeting);
                    }
                    entityManager.flush();
                    
                    // Don't reload - just return the section as-is
                    // The meetings will be loaded lazily when needed via the repository

                    updateAvailability(teacher.getId(), room.getId(), timeSlots, teacherSchedule, roomSchedule);
                    return section;
                }
            }
        }
        
        log.warn("Could not create section for course {} ({} hours/week) - no available time slots found", 
                course.getCode(), course.getHoursPerWeek());
        return null;
    }

    /**
     * Calculates how many sections are needed for a course based on demand.
     */
    public int calculateSectionsNeeded(Course course) {
        if ("core".equals(course.getCourseType())) {
            return Math.max(1, course.getHoursPerWeek() / 2);
        } else {
            return Math.max(1, course.getHoursPerWeek() / 3);
        }
    }

    private List<TimeSlot> findAvailableTimeSlots(int hoursPerWeek, Integer teacherId, Integer roomId,
                                                   Map<Integer, Map<String, List<TimeSlot>>> teacherSchedule,
                                                   Map<Integer, Map<String, List<TimeSlot>>> roomSchedule) {
        List<TimeSlot> result = new ArrayList<>();
        int remainingHours = hoursPerWeek;
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};

        // Try to allocate hours across multiple days
        // For courses with many hours, we need to distribute them across the week
        for (String day : days) {
            if (remainingHours <= 0) break;

            List<TimeSlot> teacherSlots = teacherSchedule
                    .getOrDefault(teacherId, new HashMap<>())
                    .getOrDefault(day, new ArrayList<>());
            List<TimeSlot> roomSlots = roomSchedule
                    .getOrDefault(roomId, new HashMap<>())
                    .getOrDefault(day, new ArrayList<>());

            int teacherDayHours = calculateDayHours(teacherSlots);
            if (teacherDayHours >= MAX_DAILY_TEACHER_HOURS) {
                continue; // Teacher already at max hours for this day
            }

            // Calculate how many hours we can still allocate on this day
            int availableDayHours = MAX_DAILY_TEACHER_HOURS - teacherDayHours;
            int hoursToAllocate = Math.min(remainingHours, availableDayHours);
            
            // Try to find a slot for the remaining hours (or what we can fit)
            TimeSlot slot = findAvailableSlot(day, teacherSlots, roomSlots, Math.min(hoursToAllocate, MAX_SESSION_HOURS));
            if (slot != null) {
                result.add(slot);
                remainingHours -= slot.hours;
            } else {
                // If we can't find a slot on this day, try a smaller duration
                // This helps when there are partial conflicts
                for (int tryHours = Math.min(hoursToAllocate, MAX_SESSION_HOURS); tryHours >= MIN_SESSION_HOURS; tryHours--) {
                    slot = findAvailableSlot(day, teacherSlots, roomSlots, tryHours);
                    if (slot != null) {
                        result.add(slot);
                        remainingHours -= slot.hours;
                        break;
                    }
                }
            }
        }

        // If we couldn't allocate all hours, return null (strict requirement)
        // But log why it failed for debugging
        if (remainingHours > 0) {
            log.debug("Could not allocate all {} hours for teacher {} room {}. Remaining: {}", 
                    hoursPerWeek, teacherId, roomId, remainingHours);
            return null;
        }

        return result.isEmpty() ? null : result;
    }

    private TimeSlot findAvailableSlot(String day, List<TimeSlot> teacherSlots, List<TimeSlot> roomSlots,
                                      int maxHours) {
        for (int startHour = SCHOOL_START_HOUR; startHour < SCHOOL_END_HOUR; startHour++) {
            if (startHour >= LUNCH_START_HOUR && startHour < LUNCH_END_HOUR) {
                continue;
            }

            for (int duration = Math.min(maxHours, MAX_SESSION_HOURS); duration >= MIN_SESSION_HOURS; duration--) {
                int endHour = startHour + duration;
                
                if (startHour < LUNCH_END_HOUR && endHour > LUNCH_START_HOUR) {
                    continue;
                }
                if (endHour > SCHOOL_END_HOUR) {
                    continue;
                }

                TimeSlot candidate = new TimeSlot(day, formatTime(startHour), formatTime(endHour), duration);
                if (!hasConflict(candidate, teacherSlots) && !hasConflict(candidate, roomSlots)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private boolean hasConflict(TimeSlot candidate, List<TimeSlot> existing) {
        return existing.stream().anyMatch(candidate::overlaps);
    }

    private int calculateDayHours(List<TimeSlot> slots) {
        return slots.stream().mapToInt(s -> s.hours).sum();
    }

    private void updateAvailability(Integer teacherId, Integer roomId, List<TimeSlot> timeSlots,
                                   Map<Integer, Map<String, List<TimeSlot>>> teacherSchedule,
                                   Map<Integer, Map<String, List<TimeSlot>>> roomSchedule) {
        // Update availability for each day that has a time slot
        for (TimeSlot slot : timeSlots) {
            teacherSchedule.computeIfAbsent(teacherId, k -> new HashMap<>())
                    .computeIfAbsent(slot.day, k -> new ArrayList<>())
                    .add(slot);
            
            roomSchedule.computeIfAbsent(roomId, k -> new HashMap<>())
                    .computeIfAbsent(slot.day, k -> new ArrayList<>())
                    .add(slot);
        }
    }

    private String formatTime(int hour) {
        return String.format("%02d:00", hour);
    }

    // Helper class for time slot management
    public static class TimeSlot {
        String day;
        String startTime;
        String endTime;
        int hours;

        TimeSlot(String day, String startTime, String endTime, int hours) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
            this.hours = hours;
        }

        boolean overlaps(TimeSlot other) {
            if (!day.equals(other.day)) return false;
            int start1 = parseHour(startTime);
            int end1 = parseHour(endTime);
            int start2 = parseHour(other.startTime);
            int end2 = parseHour(other.endTime);
            return !(end1 <= start2 || end2 <= start1);
        }

        private int parseHour(String time) {
            return Integer.parseInt(time.split(":")[0]);
        }
    }
}

