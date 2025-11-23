# Quick Test Reference Card

## 🚀 Quick Start Tests (5 minutes)

### Admin Tests

1. **Test Semesters Load**
   - Go to: `http://localhost:3000/admin/schedule`
   - ✅ Should see **9 semesters** in dropdown

2. **Test Schedule Generation**
   - Select: "Fall 2021" (or any semester)
   - Click: "Generate Schedule"
   - ✅ Should see table with **20-50 sections**
   - ✅ Summary shows: "Total Sections: X, Total Courses: Y"

3. **Test Schedule Display**
   - ✅ Each row shows: Course, Teacher, Room, Schedule, Enrollment
   - ✅ Times are in format: "MONDAY 09:00-11:00"
   - ✅ Enrollment shows: "0 / 10 (10 available)"

### Student Tests

4. **Test Student Page Loads**
   - Go to: `http://localhost:3000/students/1/plan`
   - ✅ Should see student info and 3 panels

5. **Test Available Sections**
   - Select: "Fall 2021" (same as admin generated)
   - ✅ Should see sections from generated schedule
   - ✅ Checkboxes should be enabled/disabled based on prerequisites/conflicts

6. **Test Enrollment**
   - Check 2 sections (no conflicts)
   - Click: "Enroll"
   - ✅ Should see success message
   - ✅ Sections move to "Enrolled Sections" panel
   - ✅ Capacity updates (e.g., "2 / 10")

---

## ✅ Critical Validation Checks

### After Generating Schedule:
- [ ] No teacher has > 4 hours/day
- [ ] No overlapping times for same teacher/room
- [ ] All times are 09:00-17:00 (no lunch 12:00-13:00)
- [ ] Each section has at least 1 meeting time

### After Student Enrollment:
- [ ] Can't enroll in > 5 courses
- [ ] Can't enroll if prerequisites missing
- [ ] Can't enroll if time conflict
- [ ] Can't enroll if section full (10 students)

---

## 📊 Expected Data

- **Semesters**: 9 total (Fall 2021, Spring 2021, Fall 2022, etc.)
- **Students**: 400 total (IDs 1-400)
- **Courses**: 57 total (20 core + 37 electives)
- **Sections Generated**: 20-50 per semester
- **Max per Section**: 10 students
- **Max per Student**: 5 courses/semester

---

## 🔍 Quick Debug

**No semesters?** → Check backend: `curl http://localhost:8080/api/admin/semesters`

**No sections?** → Generate schedule first in admin view

**Enrollment fails?** → Check browser console (F12) for errors

**Schedule empty?** → Check backend logs for constraint violations

