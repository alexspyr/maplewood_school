# UI Testing Guide - Maplewood Scheduling System

This guide provides step-by-step instructions to test all UI functionality.

## Prerequisites

1. **Start the application:**
   ```bash
   docker-compose up
   ```
   Or run locally:
   - Backend: `cd backend && mvn spring-boot:run`
   - Frontend: `cd frontend && npm run dev`

2. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

---

## Part 1: Admin Schedule Management

### Test 1: View Available Semesters

**Steps:**
1. Navigate to http://localhost:3000/admin/schedule
2. Click on the "Semester" dropdown

**Expected Results:**
- ✅ Dropdown should show **9 semesters** from the database
- ✅ Each semester should display as: `{Name} {Year}` (e.g., "Fall 2024", "Spring 2025")
- ✅ Semesters should be in chronological order
- ✅ No loading errors should appear

**Expected Semesters:**
- Fall 2024 (or similar based on your data)
- Spring 2025
- Fall 2025
- Spring 2026
- (and 5 more historical/future semesters)

---

### Test 2: Generate Master Schedule (First Time)

**Steps:**
1. Go to `/admin/schedule`
2. Select a semester from the dropdown (e.g., "Fall 2024" or any active semester)
3. Click **"Generate Schedule"** button
4. Wait for the generation to complete (should take 2-5 seconds)

**Expected Results:**
- ✅ Button should show loading spinner while generating
- ✅ After completion, a schedule table should appear
- ✅ **Schedule Summary Card** should show:
  - Total Sections: **> 0** (typically 20-50 sections)
  - Total Courses: **> 0** (typically 10-30 courses)
  - Unassigned Courses: **0 or small number** (some courses may not be assignable)
- ✅ **Schedule Table** should display columns:
  - Course (code and name)
  - Teacher (first and last name)
  - Room (classroom name)
  - Schedule (day and time slots)
  - Enrollment (X / 10 available)
- ✅ Each row should show:
  - Course code (e.g., "MATH101")
  - Teacher name
  - Room name
  - Meeting times (e.g., "MONDAY 09:00-11:00")
  - Enrollment count (0 / 10 initially)

**Validation Checks:**
- ✅ No time conflicts (same teacher/room not scheduled at same time)
- ✅ All meetings are within school hours (09:00-17:00)
- ✅ No meetings during lunch (12:00-13:00)
- ✅ Each section has at least one meeting time

---

### Test 3: Load Existing Schedule

**Steps:**
1. Go to `/admin/schedule`
2. Select a semester that already has a generated schedule
3. Click **"Load Schedule"** button

**Expected Results:**
- ✅ Schedule table should appear immediately
- ✅ Should show the same sections as when generated
- ✅ Enrollment counts should reflect current enrollments (if any students enrolled)
- ✅ No errors should appear

---

### Test 4: Regenerate Schedule (Overwrite)

**Steps:**
1. Select a semester with an existing schedule
2. Click **"Generate Schedule"** again
3. Confirm the action

**Expected Results:**
- ✅ Old schedule should be deleted
- ✅ New schedule should be generated
- ✅ Section IDs may change (new generation)
- ✅ Schedule should still be valid (no conflicts)

---

### Test 5: Schedule Validation Checks

**After generating a schedule, verify:**

1. **Teacher Constraints:**
   - ✅ No teacher has more than **4 hours per day**
   - ✅ No teacher has overlapping classes
   - ✅ Each teacher only teaches courses in their specialization

2. **Room Constraints:**
   - ✅ No room has overlapping bookings
   - ✅ Room type matches course requirements (e.g., science → lab, art → studio)
   - ✅ Room capacity is respected (max 10 students per section)

3. **Time Constraints:**
   - ✅ All classes are Monday-Friday only
   - ✅ All classes are between 09:00-17:00
   - ✅ No classes during lunch (12:00-13:00)
   - ✅ Class duration is 1-2 hours

4. **Course Coverage:**
   - ✅ Core courses should have more sections than electives
   - ✅ Courses with more hours/week should have more sections

---

## Part 2: Student Course Planning

### Test 6: Access Student Planning Page

**Steps:**
1. Navigate to http://localhost:3000/students/1/plan
   - Replace `1` with any valid student ID (1-400)

**Expected Results:**
- ✅ Page should load without errors
- ✅ Student information should be displayed
- ✅ Semester dropdown should appear
- ✅ Three panels should be visible:
  1. **Available Course Sections**
  2. **Selected Schedule**
  3. **Academic Progress**

---

### Test 7: View Available Sections

**Steps:**
1. Go to `/students/1/plan`
2. Select a semester from dropdown (preferably one with a generated schedule)
3. Wait for sections to load

**Expected Results:**
- ✅ **Available Course Sections** table should show:
  - Course code and name
  - Teacher name
  - Room name
  - Meeting times
  - Capacity remaining
  - Status indicators (checkboxes, badges)
- ✅ Each section should have a checkbox
- ✅ Sections should show status:
  - ✅ Green/available: Prerequisites met, no conflicts, has capacity
  - ⚠️ Yellow/warning: Missing prerequisites OR time conflict OR full
  - ❌ Red/disabled: Multiple issues

---

### Test 8: Prerequisite Validation

**Steps:**
1. Select a student (e.g., student ID 1)
2. Select a semester
3. Look for courses with prerequisites

**Expected Results:**
- ✅ Sections for courses with prerequisites should show:
  - "Prerequisites not met" indicator if student hasn't passed prerequisite
  - Checkbox should be disabled if prerequisites not met
- ✅ Sections for courses without prerequisites should be available
- ✅ Check the **Academic Progress** panel to see passed courses

**Test with Different Students:**
- Student with many passed courses → more sections available
- Student with few passed courses → fewer sections available

---

### Test 9: Time Conflict Detection

**Steps:**
1. Select a student
2. Select a semester
3. Check a section (e.g., "MONDAY 09:00-11:00")
4. Try to check another section with overlapping time

**Expected Results:**
- ✅ **Selected Schedule** panel should show the first section
- ✅ Second section should show "Time conflict" indicator
- ✅ Checkbox for conflicting section should be disabled or show warning
- ✅ Visual highlight should indicate the conflict

---

### Test 10: Capacity Validation

**Steps:**
1. Select a section that shows "0 available" or "Full"
2. Try to check the checkbox

**Expected Results:**
- ✅ Checkbox should be disabled
- ✅ Should show "Section is full" or similar message
- ✅ Only sections with remaining capacity should be selectable

---

### Test 11: Select Multiple Sections

**Steps:**
1. Select a student
2. Select a semester with generated schedule
3. Check multiple sections (up to 5):
   - Ensure no time conflicts
   - Ensure prerequisites are met
   - Ensure sections have capacity

**Expected Results:**
- ✅ **Selected Schedule** panel should show all selected sections
- ✅ Should display:
  - Course name
  - Teacher
  - Room
  - Meeting times
  - Visual schedule grid (if implemented)
- ✅ No conflicts should be shown
- ✅ "Enroll" button should be enabled

---

### Test 12: Enroll in Courses

**Steps:**
1. Select 2-3 sections (no conflicts, prerequisites met, has capacity)
2. Click **"Enroll"** button
3. Wait for enrollment to complete

**Expected Results:**
- ✅ Success message should appear
- ✅ Selected sections should move to "Enrolled Sections" panel
- ✅ Available sections should update:
  - Enrolled sections removed from available list
  - Capacity counts updated for remaining sections
- ✅ **Academic Progress** panel should update (if credits are counted)

---

### Test 13: Enrollment Validation Errors

**Test Case A: Too Many Courses**
1. Select 5 sections
2. Try to select a 6th section

**Expected Results:**
- ✅ Should show error: "Cannot enroll in more than 5 courses per semester"
- ✅ 6th checkbox should be disabled or show warning

**Test Case B: Missing Prerequisites**
1. Select a section requiring a prerequisite the student hasn't completed
2. Try to enroll

**Expected Results:**
- ✅ Should show error: "Missing prerequisite for course {CODE}"
- ✅ Enrollment should fail

**Test Case C: Time Conflict**
1. Select two sections with overlapping times
2. Try to enroll

**Expected Results:**
- ✅ Should show error: "Time conflict between selected course sections"
- ✅ Enrollment should fail

**Test Case D: Full Section**
1. Select a section that is already full
2. Try to enroll

**Expected Results:**
- ✅ Should show error: "Course section {ID} is full"
- ✅ Enrollment should fail

---

### Test 14: Academic Progress Display

**Steps:**
1. Go to `/students/1/plan`
2. Select any semester
3. View the **Academic Progress** panel

**Expected Results:**
- ✅ Should display:
  - **Credits Earned**: X / 30
  - **Core Courses Completed**: Y / 20
  - **GPA**: X.XX (calculated from passed courses)
  - **Projected Graduation Year**: YYYY
  - **Graduation Status**: "On track to graduate in YYYY" or "Eligible for graduation"
- ✅ Progress should be based on `student_course_history` table
- ✅ GPA calculation should be reasonable (0.0 - 4.0)

**Test with Different Students:**
- Student with many passed courses → higher credits, closer to graduation
- Student with few passed courses → lower credits, further from graduation

---

### Test 15: View Enrolled Sections

**Steps:**
1. After enrolling in courses (Test 12)
2. View the **Enrolled Sections** panel

**Expected Results:**
- ✅ Should show all enrolled sections for the selected semester
- ✅ Should display:
  - Course name
  - Teacher
  - Room
  - Meeting times
- ✅ Should match what was in "Selected Schedule" before enrollment

---

## Part 3: Cross-Feature Testing

### Test 16: Schedule Generation → Student Enrollment Flow

**Steps:**
1. **Admin**: Generate schedule for "Fall 2024"
2. **Student**: Go to `/students/1/plan`
3. **Student**: Select "Fall 2024" semester
4. **Student**: Verify sections from generated schedule appear
5. **Student**: Enroll in 2-3 sections
6. **Admin**: Go back to `/admin/schedule`
7. **Admin**: Load "Fall 2024" schedule
8. **Admin**: Check enrollment counts

**Expected Results:**
- ✅ Student should see sections that were generated
- ✅ After enrollment, admin should see updated enrollment counts
- ✅ Enrollment counts should match (e.g., "2 / 10" if 2 students enrolled)

---

### Test 17: Multiple Students Enrolling

**Steps:**
1. Generate schedule for a semester
2. Have Student 1 enroll in section A
3. Have Student 2 enroll in section A
4. Have Student 3 enroll in section A
5. Check capacity updates

**Expected Results:**
- ✅ Each enrollment should update capacity
- ✅ After 10 enrollments, section should show "Full"
- ✅ 11th student should not be able to enroll

---

### Test 18: Semester Selection Persistence

**Steps:**
1. Select a semester
2. Generate/load schedule
3. Navigate away and come back
4. Select different semester
5. Navigate away and come back

**Expected Results:**
- ✅ Semester selection should reset (not persisted across sessions - this is expected)
- ✅ Each visit should start fresh

---

## Part 4: Error Handling

### Test 19: Invalid Semester Selection

**Steps:**
1. Try to generate schedule without selecting semester
2. Click "Generate Schedule"

**Expected Results:**
- ✅ Should show error: "Please select a semester"
- ✅ Button should be disabled or show validation error

---

### Test 20: Network/API Errors

**Steps:**
1. Stop the backend server
2. Try to generate schedule
3. Try to load schedule
4. Try to enroll

**Expected Results:**
- ✅ Should show user-friendly error messages
- ✅ Should not crash the UI
- ✅ Error should indicate connection issue

---

## Part 5: UI/UX Validation

### Test 21: Visual Design

**Check:**
- ✅ Light theme (not dark background)
- ✅ Content is centered
- ✅ Header at top, footer at bottom
- ✅ Tables are readable
- ✅ Buttons are clearly visible
- ✅ Loading indicators appear during operations
- ✅ Success/error messages are clear

---

### Test 22: Responsive Design (Optional)

**Check:**
- ✅ UI works on different screen sizes
- ✅ Tables are scrollable on mobile
- ✅ Buttons are accessible on touch devices

---

## Quick Test Checklist

Use this checklist for a quick smoke test:

- [ ] Admin page loads
- [ ] Semesters dropdown shows 9 semesters
- [ ] Can generate schedule for a semester
- [ ] Schedule table appears with sections
- [ ] Student page loads (e.g., `/students/1/plan`)
- [ ] Available sections appear
- [ ] Can select sections (checkboxes work)
- [ ] Can enroll in courses
- [ ] Enrollment updates capacity
- [ ] Academic progress displays correctly
- [ ] Error messages appear for invalid actions

---

## Expected Data Ranges

Based on the database:

- **Students**: 400 total
- **Teachers**: 50 total
- **Courses**: 57 total (20 core + 37 electives)
- **Classrooms**: 60 total
- **Semesters**: 9 total

**Typical Schedule Generation Results:**
- Sections generated: 20-50 (depends on course hours and constraints)
- Courses covered: 10-30 (some may be unassignable)
- Unassigned courses: 0-10 (due to resource constraints)

---

## Troubleshooting

**Issue**: No semesters appear in dropdown
- **Fix**: Check backend is running, check `/api/admin/semesters` endpoint

**Issue**: Schedule generation fails
- **Fix**: Check backend logs, verify database has courses/teachers/classrooms

**Issue**: Student can't see sections
- **Fix**: Ensure schedule was generated for that semester first

**Issue**: Enrollment fails silently
- **Fix**: Check browser console for errors, check backend logs

---

## Notes

- All times are in 24-hour format (09:00, not 9:00 AM)
- School hours: 09:00-17:00 with lunch break 12:00-13:00
- Max courses per student per semester: 5
- Max students per section: 10
- Max teacher hours per day: 4

