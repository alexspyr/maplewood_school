-- Create course_section_meetings table
CREATE TABLE IF NOT EXISTS course_section_meetings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_section_id INTEGER NOT NULL,
    day_of_week TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    FOREIGN KEY (course_section_id) REFERENCES course_sections(id) ON DELETE CASCADE,
    CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY')),
    CHECK (start_time < end_time)
);

CREATE INDEX IF NOT EXISTS idx_meetings_section_id ON course_section_meetings(course_section_id);
CREATE INDEX IF NOT EXISTS idx_meetings_day_time ON course_section_meetings(day_of_week, start_time, end_time);

