# Database Schema Review - Maplewood Scheduling System

## ✅ Tables Created for Challenges

### Challenge 1: Master Schedule Generator

#### 1. `course_sections`
**Purpose**: Stores course section assignments (teacher, room, semester for each course section)

**Columns**:
- `id` (PK)
- `course_id` (FK → courses)
- `teacher_id` (FK → teachers)
- `classroom_id` (FK → classrooms)
- `semester_id` (FK → semesters)
- `capacity` (default 10, max 10)
- `created_at` (TEXT)

**Status**: ✅ Created via V1 migration

#### 2. `course_section_meetings`
**Purpose**: Stores time slots for each course section (day, start_time, end_time)

**Columns**:
- `id` (PK)
- `course_section_id` (FK → course_sections, CASCADE DELETE)
- `day_of_week` (MONDAY-FRIDAY)
- `start_time` (TEXT, format: "HH:mm")
- `end_time` (TEXT, format: "HH:mm")

**Status**: ✅ Created via V2 migration

### Challenge 2: Student Course Planning

#### 3. `student_course_enrollments`
**Purpose**: Tracks which students are enrolled in which course sections

**Columns**:
- `id` (PK)
- `student_id` (FK → students)
- `course_section_id` (FK → course_sections, CASCADE DELETE)
- `enrolled_at` (TEXT)
- UNIQUE constraint on (student_id, course_section_id)

**Status**: ✅ Created via V3 migration

## ✅ Existing Tables (Pre-populated)

The database already contains:

1. **`students`** - 400 students across 4 grade levels
2. **`teachers`** - 50 teachers with specializations
3. **`courses`** - 57 courses (20 core + 37 electives)
4. **`classrooms`** - 60 classrooms with various types
5. **`semesters`** - 9 semesters (6 historical + current + 2 future)
6. **`student_course_history`** - ~6,700 records for prerequisite validation
7. **`specializations`** - 9 subject areas
8. **`room_types`** - 8 different room types

## 📊 Data Flow

### Schedule Generation Flow:
1. Admin selects a semester
2. System generates `course_sections` (assigns teachers, rooms)
3. System creates `course_section_meetings` (assigns time slots)
4. Sections appear in UI with enrollment counts

### Student Enrollment Flow:
1. Student views available `course_sections` for a semester
2. System validates prerequisites using `student_course_history`
3. System checks time conflicts with existing `student_course_enrollments`
4. On enrollment, creates `student_course_enrollments` record
5. Enrollment count updates in `course_sections` view

## 🎯 Data Requirements Analysis

### Do we need seed/fake data?

**NO** - We don't need to prefill fake data because:

1. **Course Sections**: Created dynamically by the schedule generation algorithm
2. **Meetings**: Created automatically when sections are generated
3. **Enrollments**: Created when students enroll through the UI

### What data IS needed (and already exists):

✅ **Students** - 400 students (already in database)
✅ **Teachers** - 50 teachers (already in database)
✅ **Courses** - 57 courses (already in database)
✅ **Classrooms** - 60 classrooms (already in database)
✅ **Semesters** - 9 semesters (already in database)
✅ **Student Course History** - ~6,700 records (already in database, needed for prerequisites)

## 🔍 Verification Checklist

- [x] `course_sections` table created with proper FKs
- [x] `course_section_meetings` table created with proper FKs
- [x] `student_course_enrollments` table created with proper FKs
- [x] All indexes created for performance
- [x] Constraints in place (capacity limits, unique enrollments)
- [x] CASCADE DELETE configured for data integrity
- [x] Date/time fields use TEXT for SQLite compatibility
- [x] Custom converters added for LocalDate/LocalDateTime

## 📝 Notes

- Tables start empty and are populated by the application logic
- Schedule generation creates sections and meetings on-demand
- Student enrollments are created through the enrollment API
- All foreign key relationships are properly configured
- The existing database provides all foundational data needed

## 🚀 Next Steps

The database schema is complete and ready for use. The application will:
1. Generate schedules → populate `course_sections` and `course_section_meetings`
2. Handle enrollments → populate `student_course_enrollments`
3. Track progress → use existing `student_course_history` for prerequisites

No additional seed data is required!

