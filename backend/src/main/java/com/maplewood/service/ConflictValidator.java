package com.maplewood.service;

import com.maplewood.entity.CourseSectionMeeting;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility class for validating time conflicts.
 * Follows Single Responsibility Principle - only handles conflict detection logic.
 */
@Component
public class ConflictValidator {

    /**
     * Checks if two sets of meetings have time conflicts.
     */
    public boolean hasTimeConflict(List<CourseSectionMeeting> newMeetings, 
                                   List<CourseSectionMeeting> existingMeetings) {
        if (newMeetings == null || existingMeetings == null) {
            return false;
        }

        for (CourseSectionMeeting newMeeting : newMeetings) {
            for (CourseSectionMeeting existingMeeting : existingMeetings) {
                if (isConflict(newMeeting, existingMeeting)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if two individual meetings conflict.
     */
    public boolean isConflict(CourseSectionMeeting meeting1, CourseSectionMeeting meeting2) {
        if (meeting1.getDayOfWeek() != meeting2.getDayOfWeek()) {
            return false;
        }
        return timeOverlaps(meeting1.getStartTime(), meeting1.getEndTime(),
                          meeting2.getStartTime(), meeting2.getEndTime());
    }

    private boolean timeOverlaps(String start1, String end1, String start2, String end2) {
        int start1Hour = parseHour(start1);
        int end1Hour = parseHour(end1);
        int start2Hour = parseHour(start2);
        int end2Hour = parseHour(end2);
        
        return !(end1Hour <= start2Hour || end2Hour <= start1Hour);
    }

    private int parseHour(String time) {
        return Integer.parseInt(time.split(":")[0]);
    }
}

