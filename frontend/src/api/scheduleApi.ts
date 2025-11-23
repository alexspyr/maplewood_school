import { apiClient } from './client';
import type { ScheduleResponse, GenerateScheduleRequest, TeacherWorkload, RoomUsage, Semester } from '../types';

export const scheduleApi = {
  generateSchedule: async (request: GenerateScheduleRequest): Promise<ScheduleResponse> => {
    const response = await apiClient.post<ScheduleResponse>('/admin/schedules/generate', request);
    return response.data;
  },

  getSchedule: async (semesterId: number): Promise<ScheduleResponse> => {
    const response = await apiClient.get<ScheduleResponse>(`/admin/schedules/${semesterId}`);
    return response.data;
  },

  getTeacherWorkload: async (semesterId: number): Promise<TeacherWorkload[]> => {
    const response = await apiClient.get<TeacherWorkload[]>(`/admin/teachers/workload?semesterId=${semesterId}`);
    return response.data;
  },

  getRoomUsage: async (semesterId: number): Promise<RoomUsage[]> => {
    const response = await apiClient.get<RoomUsage[]>(`/admin/rooms/usage?semesterId=${semesterId}`);
    return response.data;
  },

  getSemesters: async (): Promise<Semester[]> => {
    const response = await apiClient.get<Semester[]>('/admin/semesters');
    return response.data;
  },
};

