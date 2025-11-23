-- Create course_sections table
CREATE TABLE IF NOT EXISTS course_sections (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_id INTEGER NOT NULL,
    teacher_id INTEGER NOT NULL,
    classroom_id INTEGER NOT NULL,
    semester_id INTEGER NOT NULL,
    capacity INTEGER NOT NULL DEFAULT 10,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    FOREIGN KEY (classroom_id) REFERENCES classrooms(id),
    FOREIGN KEY (semester_id) REFERENCES semesters(id),
    CHECK (capacity > 0 AND capacity <= 10)
);

CREATE INDEX IF NOT EXISTS idx_course_sections_course_id ON course_sections(course_id);
CREATE INDEX IF NOT EXISTS idx_course_sections_teacher_id ON course_sections(teacher_id);
CREATE INDEX IF NOT EXISTS idx_course_sections_classroom_id ON course_sections(classroom_id);
CREATE INDEX IF NOT EXISTS idx_course_sections_semester_id ON course_sections(semester_id);

