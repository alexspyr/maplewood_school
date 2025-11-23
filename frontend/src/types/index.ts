export interface Semester {
  id: number;
  name: string;
  year: number;
  orderInYear: number;
  isActive: boolean;
}

export interface Course {
  id: number;
  code: string;
  name: string;
  credits: number;
  hoursPerWeek: number;
  courseType: string;
  semesterOrder: number;
}

export interface Teacher {
  id: number;
  firstName: string;
  lastName: string;
  email?: string;
}

export interface Classroom {
  id: number;
  name: string;
  capacity: number;
  roomType?: string;
}

export interface Meeting {
  id: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
}

export interface CourseSection {
  id: number;
  course: Course;
  teacher: Teacher;
  classroom: Classroom;
  meetings: Meeting[];
  capacity: number;
  enrolledCount: number;
  remainingCapacity: number;
}

export interface ScheduleResponse {
  semesterId: number;
  semesterName: string;
  sections: CourseSection[];
  summary: {
    totalSections: number;
    totalCourses: number;
    unassignedCourses: number;
    message: string;
  };
}

export interface AvailableSection extends CourseSection {
  prerequisitesMet: boolean;
  hasTimeConflict: boolean;
  conflictReason?: string;
}

export interface EnrolledSection {
  id: number;
  course: Course;
  teacher: Teacher;
  classroom: Classroom;
  meetings: Meeting[];
}

export interface AcademicProgress {
  creditsEarned: number;
  creditsRequired: number;
  coreCoursesCompleted: number;
  coreCoursesRequired: number;
  gpa: number;
  graduationStatus: string;
  projectedGraduationYear: number;
}

export interface Student {
  id: number;
  firstName: string;
  lastName: string;
  gradeLevel: number;
  email?: string;
}

export interface StudentPlanResponse {
  student: Student;
  semesterId: number;
  semesterName: string;
  availableSections: AvailableSection[];
  enrolledSections: EnrolledSection[];
  progress: AcademicProgress;
}

export interface EnrollRequest {
  semesterId: number;
  courseSectionIds: number[];
}

export interface EnrollResponse {
  success: boolean;
  message: string;
  errors?: string[];
  enrolledSectionIds?: number[];
}

export interface TeacherWorkload {
  teacherId: number;
  teacherName: string;
  totalHoursPerWeek: number;
  hoursPerDay: Record<string, number>;
  sectionCount: number;
}

export interface RoomUsage {
  roomId: number;
  roomName: string;
  totalSections: number;
  totalHoursPerWeek: number;
  utilizationPercentage: number;
}

export interface GenerateScheduleRequest {
  semesterId: number;
}

