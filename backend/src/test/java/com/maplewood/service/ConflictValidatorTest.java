package com.maplewood.service;

import com.maplewood.entity.CourseSectionMeeting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConflictValidatorTest {

    private ConflictValidator conflictValidator;

    @BeforeEach
    void setUp() {
        conflictValidator = new ConflictValidator();
    }

    @Test
    void testNoConflict_DifferentDays() {
        List<CourseSectionMeeting> newMeetings = List.of(
                createMeeting(CourseSectionMeeting.DayOfWeek.MONDAY, "09:00", "10:00")
        );
        List<CourseSectionMeeting> existingMeetings = List.of(
                createMeeting(CourseSectionMeeting.DayOfWeek.TUESDAY, "09:00", "10:00")
        );

        assertFalse(conflictValidator.hasTimeConflict(newMeetings, existingMeetings));
    }

    @Test
    void testNoConflict_SameDay_NonOverlapping() {
        List<CourseSectionMeeting> newMeetings = List.of(
                createMeeting(CourseSectionMeeting.DayOfWeek.MONDAY, "09:00", "10:00")
        );
        List<CourseSectionMeeting> existingMeetings = List.of(
                createMeeting(CourseSectionMeeting.DayOfWeek.MONDAY, "10:00", "11:00")
        );

        assertFalse(conflictValidator.hasTimeConflict(newMeetings, existingMeetings));
    }

    @Test
    void testConflict_SameDay_Overlapping() {
        List<CourseSectionMeeting> newMeetings = List.of(
                createMeeting(CourseSectionMeeting.DayOfWeek.MONDAY, "09:00", "10:00")
        );
        List<CourseSectionMeeting> existingMeetings = List.of(
                createMeeting(CourseSectionMeeting.DayOfWeek.MONDAY, "09:30", "10:30")
        );

        assertTrue(conflictValidator.hasTimeConflict(newMeetings, existingMeetings));
    }

    @Test
    void testConflict_SameTime() {
        List<CourseSectionMeeting> newMeetings = List.of(
                createMeeting(CourseSectionMeeting.DayOfWeek.MONDAY, "09:00", "10:00")
        );
        List<CourseSectionMeeting> existingMeetings = List.of(
                createMeeting(CourseSectionMeeting.DayOfWeek.MONDAY, "09:00", "10:00")
        );

        assertTrue(conflictValidator.hasTimeConflict(newMeetings, existingMeetings));
    }

    @Test
    void testNoConflict_EmptyLists() {
        assertFalse(conflictValidator.hasTimeConflict(new ArrayList<>(), new ArrayList<>()));
        assertFalse(conflictValidator.hasTimeConflict(null, null));
    }

    private CourseSectionMeeting createMeeting(CourseSectionMeeting.DayOfWeek day, String start, String end) {
        CourseSectionMeeting meeting = new CourseSectionMeeting();
        meeting.setDayOfWeek(day);
        meeting.setStartTime(start);
        meeting.setEndTime(end);
        return meeting;
    }
}

