import { useState, useEffect } from 'react';
import {
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Box,
  Alert,
  CircularProgress,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Card,
  CardContent,
} from '@mui/material';
import { scheduleApi } from '../api/scheduleApi';
import type { ScheduleResponse, Semester } from '../types';

export default function AdminSchedulePage() {
  const [semesterId, setSemesterId] = useState<number | ''>('');
  const [schedule, setSchedule] = useState<ScheduleResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [generating, setGenerating] = useState(false);
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
        setError('Failed to load semesters: ' + (err.response?.data?.message || err.message));
      } finally {
        setLoadingSemesters(false);
      }
    };
    fetchSemesters();
  }, []);

  const handleGenerate = async () => {
    if (!semesterId) {
      setError('Please select a semester');
      return;
    }

    setGenerating(true);
    setError(null);
    try {
      const response = await scheduleApi.generateSchedule({ semesterId: Number(semesterId) });
      setSchedule(response);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to generate schedule');
    } finally {
      setGenerating(false);
    }
  };

  const handleLoadSchedule = async () => {
    if (!semesterId) {
      setError('Please select a semester');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const response = await scheduleApi.getSchedule(Number(semesterId));
      setSchedule(response);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load schedule');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (semesterId) {
      handleLoadSchedule();
    } else {
      // Clear schedule when no semester is selected
      setSchedule(null);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [semesterId]);

  return (
    <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      <Typography variant="h4" gutterBottom sx={{ textAlign: 'center', mb: 3, color: '#1976d2' }}>
        Master Schedule Generator
      </Typography>

      <Box sx={{ mb: 3, display: 'flex', gap: 2, alignItems: 'center', justifyContent: 'center', flexWrap: 'wrap' }}>
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
        <Button
          variant="contained"
          onClick={handleGenerate}
          disabled={!semesterId || generating}
        >
          {generating ? <CircularProgress size={24} /> : 'Generate Schedule'}
        </Button>
        <Button
          variant="outlined"
          onClick={handleLoadSchedule}
          disabled={!semesterId || loading}
        >
          {loading ? <CircularProgress size={24} /> : 'Load Schedule'}
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {schedule && (
        <Box sx={{ width: '100%', maxWidth: '1400px' }}>
          <Card sx={{ mb: 3, backgroundColor: '#ffffff', color: '#000000' }}>
            <CardContent sx={{ backgroundColor: '#ffffff', color: '#000000' }}>
              <Typography variant="h6" gutterBottom sx={{ color: '#000000' }}>
                Schedule Summary - {schedule.semesterName}
              </Typography>
              <Typography sx={{ color: '#000000' }}>
                Total Sections: {schedule.summary.totalSections} | Total Courses: {schedule.summary.totalCourses} | 
                Unassigned: {schedule.summary.unassignedCourses}
              </Typography>
              <Typography variant="body2" sx={{ mt: 1, color: 'rgba(0, 0, 0, 0.6)' }}>
                {schedule.summary.message}
              </Typography>
            </CardContent>
          </Card>

          {schedule.sections.length === 0 ? (
            <Alert severity="info" sx={{ mb: 2 }}>
              No sections found for this semester. Generate a schedule to create course sections.
            </Alert>
          ) : (
            <TableContainer 
              component={Paper} 
              sx={{ 
                backgroundColor: '#ffffff',
                color: '#000000',
                boxShadow: 2
              }}
            >
              <Table sx={{ backgroundColor: '#ffffff' }}>
                <TableHead>
                  <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
                    <TableCell sx={{ color: '#000000', fontWeight: 600 }}>Course</TableCell>
                    <TableCell sx={{ color: '#000000', fontWeight: 600 }}>Teacher</TableCell>
                    <TableCell sx={{ color: '#000000', fontWeight: 600 }}>Room</TableCell>
                    <TableCell sx={{ color: '#000000', fontWeight: 600 }}>Schedule</TableCell>
                    <TableCell sx={{ color: '#000000', fontWeight: 600 }}>Enrollment</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {schedule.sections.map((section) => (
                    <TableRow key={section.id} sx={{ backgroundColor: '#ffffff' }}>
                      <TableCell sx={{ color: '#000000' }}>
                        <strong>{section.course?.code || 'N/A'}</strong> - {section.course?.name || 'Unknown Course'}
                      </TableCell>
                      <TableCell sx={{ color: '#000000' }}>
                        {section.teacher?.firstName || ''} {section.teacher?.lastName || 'Unknown Teacher'}
                      </TableCell>
                      <TableCell sx={{ color: '#000000' }}>{section.classroom?.name || 'Unknown Room'}</TableCell>
                      <TableCell sx={{ color: '#000000' }}>
                        {section.meetings && section.meetings.length > 0 ? (
                          section.meetings.map((m, idx) => (
                            <div key={idx}>
                              {m.dayOfWeek} {m.startTime}-{m.endTime}
                            </div>
                          ))
                        ) : (
                          <Typography variant="body2" color="text.secondary">No meetings scheduled</Typography>
                        )}
                      </TableCell>
                      <TableCell sx={{ color: '#000000' }}>
                        {section.enrolledCount || 0} / {section.capacity || 0} ({section.remainingCapacity || 0} available)
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </Box>
      )}
    </Box>
  );
}

