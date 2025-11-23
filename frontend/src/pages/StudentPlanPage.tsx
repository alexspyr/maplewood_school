import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import {
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Box,
  Alert,
  CircularProgress,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Card,
  CardContent,
  Checkbox,
  Chip,
} from '@mui/material';
import { studentApi } from '../api/studentApi';
import { scheduleApi } from '../api/scheduleApi';
import type { StudentPlanResponse, EnrollRequest, Semester } from '../types';

export default function StudentPlanPage() {
  const { studentId } = useParams<{ studentId: string }>();
  const [semesterId, setSemesterId] = useState<number | ''>('');
  const [plan, setPlan] = useState<StudentPlanResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedSections, setSelectedSections] = useState<Set<number>>(new Set());
  const [enrolling, setEnrolling] = useState(false);
  const [semesters, setSemesters] = useState<Semester[]>([]);
  const [loadingSemesters, setLoadingSemesters] = useState(true);

  // Fetch semesters from API
  useEffect(() => {
    const fetchSemesters = async () => {
      try {
        setLoadingSemesters(true);
        const data = await scheduleApi.getSemesters();
        setSemesters(data);
      } catch (err: any) {
        console.error('Failed to load semesters:', err);
      } finally {
        setLoadingSemesters(false);
      }
    };
    fetchSemesters();
  }, []);

  const loadPlan = async () => {
    if (!studentId || !semesterId) return;

    setLoading(true);
    setError(null);
    try {
      const response = await studentApi.getStudentPlan(Number(studentId), Number(semesterId));
      setPlan(response);
      // Pre-select enrolled sections
      const enrolledIds = new Set(response.enrolledSections.map((s) => s.id));
      setSelectedSections(enrolledIds);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load student plan');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (semesterId && studentId) {
      loadPlan();
    }
  }, [semesterId, studentId]);

  const handleSectionToggle = (sectionId: number, available: boolean) => {
    if (!available) return;

    setSelectedSections((prev) => {
      const next = new Set(prev);
      if (next.has(sectionId)) {
        next.delete(sectionId);
      } else {
        if (next.size >= 5) {
          setError('Maximum 5 courses per semester');
          return prev;
        }
        next.add(sectionId);
      }
      return next;
    });
  };

  const onSubmit = async () => {
    if (!studentId || !semesterId || selectedSections.size === 0) {
      setError('Please select at least one course section');
      return;
    }

    setEnrolling(true);
    setError(null);
    try {
      const request: EnrollRequest = {
        semesterId: Number(semesterId),
        courseSectionIds: Array.from(selectedSections),
      };
      const response = await studentApi.enrollStudent(Number(studentId), request);
      
      if (response.success) {
        setError(null);
        await loadPlan(); // Reload to show updated enrollments
      } else {
        setError(response.errors?.join(', ') || response.message);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to enroll');
    } finally {
      setEnrolling(false);
    }
  };

  return (
    <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      <Typography variant="h4" gutterBottom sx={{ textAlign: 'center', mb: 3, color: '#1976d2' }}>
        Student Course Planning
      </Typography>

      {studentId && (
        <Box sx={{ mb: 3 }}>
          <FormControl sx={{ minWidth: 200 }}>
            <InputLabel>Semester</InputLabel>
            {loadingSemesters ? (
              <Select disabled value="" label="Loading...">
                <MenuItem value="">Loading semesters...</MenuItem>
              </Select>
            ) : (
              <Select
                value={semesterId}
                label="Semester"
                onChange={(e) => setSemesterId(e.target.value as number)}
              >
                {semesters.map((sem) => (
                  <MenuItem key={sem.id} value={sem.id}>
                    {sem.name} {sem.year}
                  </MenuItem>
                ))}
              </Select>
            )}
          </FormControl>
        </Box>
      )}

      {error && (
        <Alert severity={error.includes('Successfully') ? 'success' : 'error'} sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {loading && <CircularProgress />}

      {plan && (
        <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 3, width: '100%', maxWidth: '1400px', justifyContent: 'center' }}>
          <Box sx={{ flex: { xs: '1 1 100%', md: '2 1 0%' } }}>
            <Card sx={{ mb: 3, backgroundColor: '#ffffff', color: '#000000' }}>
              <CardContent sx={{ backgroundColor: '#ffffff', color: '#000000' }}>
                <Typography variant="h6" gutterBottom sx={{ color: '#000000' }}>
                  Available Course Sections
                </Typography>
                {plan.availableSections.length === 0 ? (
                  <Alert severity="info" sx={{ mt: 2 }}>
                    No course sections available for this semester. Please generate a schedule in the admin view first.
                  </Alert>
                ) : (
                  <TableContainer sx={{ backgroundColor: '#ffffff' }}>
                    <Table size="small" sx={{ backgroundColor: '#ffffff' }}>
                      <TableHead>
                        <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
                          <TableCell padding="checkbox" sx={{ color: '#000000', fontWeight: 600 }}>Select</TableCell>
                          <TableCell sx={{ color: '#000000', fontWeight: 600 }}>Course</TableCell>
                          <TableCell sx={{ color: '#000000', fontWeight: 600 }}>Teacher</TableCell>
                          <TableCell sx={{ color: '#000000', fontWeight: 600 }}>Room</TableCell>
                          <TableCell sx={{ color: '#000000', fontWeight: 600 }}>Schedule</TableCell>
                          <TableCell sx={{ color: '#000000', fontWeight: 600 }}>Status</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {plan.availableSections.map((section) => {
                        const canSelect = 
                          section.prerequisitesMet &&
                          !section.hasTimeConflict &&
                          section.remainingCapacity > 0;
                        const isSelected = selectedSections.has(section.id);

                        return (
                          <TableRow key={section.id} sx={{ backgroundColor: '#ffffff' }}>
                            <TableCell padding="checkbox" sx={{ color: '#000000' }}>
                              <Checkbox
                                checked={isSelected}
                                disabled={!canSelect}
                                onChange={() => handleSectionToggle(section.id, canSelect)}
                              />
                            </TableCell>
                            <TableCell sx={{ color: '#000000' }}>
                              <strong>{section.course.code}</strong> - {section.course.name}
                            </TableCell>
                            <TableCell sx={{ color: '#000000' }}>
                              {section.teacher.firstName} {section.teacher.lastName}
                            </TableCell>
                            <TableCell sx={{ color: '#000000' }}>{section.classroom.name}</TableCell>
                            <TableCell sx={{ color: '#000000' }}>
                              {section.meetings.map((m, idx) => (
                                <div key={idx}>
                                  {m.dayOfWeek} {m.startTime}-{m.endTime}
                                </div>
                              ))}
                            </TableCell>
                            <TableCell sx={{ color: '#000000' }}>
                              {!section.prerequisitesMet && (
                                <Chip label="Prereq Missing" color="error" size="small" />
                              )}
                              {section.hasTimeConflict && (
                                <Chip label="Time Conflict" color="warning" size="small" />
                              )}
                              {section.remainingCapacity <= 0 && (
                                <Chip label="Full" color="default" size="small" />
                              )}
                              {canSelect && (
                                <Chip label={`${section.remainingCapacity} spots`} color="success" size="small" />
                              )}
                            </TableCell>
                          </TableRow>
                        );
                      })}
                      </TableBody>
                    </Table>
                  </TableContainer>
                )}
              </CardContent>
            </Card>
          </Box>

          <Box sx={{ flex: { xs: '1 1 100%', md: '1 1 0%' }, minWidth: { md: '300px' } }}>
            <Card sx={{ mb: 3, backgroundColor: '#ffffff', color: '#000000' }}>
              <CardContent sx={{ backgroundColor: '#ffffff', color: '#000000' }}>
                <Typography variant="h6" gutterBottom sx={{ color: '#000000' }}>
                  Selected Schedule
                </Typography>
                {selectedSections.size === 0 ? (
                  <Typography color="text.secondary">No courses selected</Typography>
                ) : (
                  <Box>
                    {plan.availableSections
                      .filter((s) => selectedSections.has(s.id))
                      .map((section) => (
                        <Box key={section.id} sx={{ mb: 2, p: 1, border: '1px solid #ddd', borderRadius: 1 }}>
                          <Typography variant="subtitle2">
                            {section.course.code} - {section.course.name}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {section.meetings.map((m) => `${m.dayOfWeek} ${m.startTime}-${m.endTime}`).join(', ')}
                          </Typography>
                        </Box>
                      ))}
                    <Button
                      variant="contained"
                      fullWidth
                      onClick={onSubmit}
                      disabled={enrolling || selectedSections.size === 0}
                    >
                      {enrolling ? <CircularProgress size={24} /> : `Enroll in ${selectedSections.size} Course(s)`}
                    </Button>
                  </Box>
                )}
              </CardContent>
            </Card>

            <Card sx={{ backgroundColor: '#ffffff', color: '#000000' }}>
              <CardContent sx={{ backgroundColor: '#ffffff', color: '#000000' }}>
                <Typography variant="h6" gutterBottom sx={{ color: '#000000' }}>
                  Academic Progress
                </Typography>
                <Typography sx={{ color: '#000000' }}>
                  <strong>Credits:</strong> {plan.progress.creditsEarned} / {plan.progress.creditsRequired}
                </Typography>
                <Typography sx={{ color: '#000000' }}>
                  <strong>Core Courses:</strong> {plan.progress.coreCoursesCompleted} / {plan.progress.coreCoursesRequired}
                </Typography>
                <Typography sx={{ color: '#000000' }}>
                  <strong>GPA:</strong> {plan.progress.gpa.toFixed(2)}
                </Typography>
                <Typography variant="body2" sx={{ mt: 1, color: 'rgba(0, 0, 0, 0.6)' }}>
                  {plan.progress.graduationStatus}
                </Typography>
              </CardContent>
            </Card>
          </Box>
        </Box>
      )}
    </Box>
  );
}

