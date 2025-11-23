import { apiClient } from './client';
import type { StudentPlanResponse, EnrollRequest, EnrollResponse, AcademicProgress } from '../types';

export const studentApi = {
  getStudentPlan: async (studentId: number, semesterId: number): Promise<StudentPlanResponse> => {
    const response = await apiClient.get<StudentPlanResponse>(
      `/students/${studentId}/plan?semesterId=${semesterId}`
    );
    return response.data;
  },

  enrollStudent: async (studentId: number, request: EnrollRequest): Promise<EnrollResponse> => {
    const response = await apiClient.post<EnrollResponse>(`/students/${studentId}/enroll`, request);
    return response.data;
  },

  getStudentProgress: async (studentId: number): Promise<AcademicProgress> => {
    const response = await apiClient.get<AcademicProgress>(`/students/${studentId}/progress`);
    return response.data;
  },
};

