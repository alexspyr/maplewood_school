-- Create student_course_enrollments table
CREATE TABLE IF NOT EXISTS student_course_enrollments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    course_section_id INTEGER NOT NULL,
    enrolled_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_section_id) REFERENCES course_sections(id) ON DELETE CASCADE,
    UNIQUE(student_id, course_section_id)
);

CREATE INDEX IF NOT EXISTS idx_enrollments_student_id ON student_course_enrollments(student_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_section_id ON student_course_enrollments(course_section_id);

