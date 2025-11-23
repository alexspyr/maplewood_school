# Maplewood High School Scheduling System - Setup Guide

This is a full-stack application for automated master schedule generation and student course planning.

## Prerequisites

### Option 1: Local Development
- **Java 17+** (for backend)
- **Maven 3.6+** (for backend)
- **Node.js 18+** and **npm** (for frontend)
- **SQLite** (database file is provided: `maplewood_school.sqlite`)

### Option 2: Docker (Recommended)
- **Docker** 20.10+
- **Docker Compose** 2.0+

## Project Structure

```
ebay_1/
├── backend/          # Spring Boot application
│   ├── Dockerfile    # Backend Docker image
│   └── .dockerignore
├── frontend/         # React + TypeScript application
│   ├── Dockerfile    # Frontend Docker image
│   ├── nginx.conf    # Nginx configuration
│   └── .dockerignore
├── docker-compose.yml # Docker Compose configuration
├── maplewood_school.sqlite  # SQLite database
├── README.md         # Project overview
├── DATABASE.md       # Database documentation
└── SETUP.md          # This file
```

## Quick Start with Docker (Recommended)

**Important**: The Docker setup uses the pre-populated `maplewood_school.sqlite` database file from the project root. This database contains:
- 400 students
- 50 teachers  
- 57 courses
- 60 classrooms
- 9 semesters
- ~6,700 student course history records

The database is mounted as a volume, so any changes (like Flyway migrations or new enrollments) will persist.

1. **Verify database is present:**
   ```bash
   ./verify-database.sh
   ```

2. **Build and start all services:**
   ```bash
   docker-compose up --build
   ```

3. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

3. **Stop services:**
   ```bash
   docker-compose down
   ```

4. **View logs:**
   ```bash
   docker-compose logs -f
   ```

**Note:** The database file is mounted as a read-only volume. If you need to modify the database, stop the containers first.

## Local Development Setup

### Backend Setup

1. **Navigate to backend directory:**
   ```bash
   cd backend
   ```

2. **Ensure the database file is accessible:**
   The application expects `maplewood_school.sqlite` in the project root directory (one level up from `backend/`).
   
   If needed, update the database path in `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:sqlite:../maplewood_school.sqlite
   ```

3. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

4. **Verify backend is running:**
   ```bash
   curl http://localhost:8080/api/admin/schedules/1
   ```

### Frontend Setup

1. **Navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start development server:**
   ```bash
   npm run dev
   ```

   The frontend will start on `http://localhost:5173` (or another port if 5173 is busy)

4. **Access the application:**
   - Admin Schedule: http://localhost:5173/admin/schedule
   - Student Planner: http://localhost:5173/students/1/plan

## Docker Configuration

### Environment Variables

The `docker-compose.yml` file uses environment variables for configuration:

- `SPRING_DATASOURCE_URL`: Database connection URL (default: `jdbc:sqlite:/app/maplewood_school.sqlite`)
- `VITE_API_BASE_URL`: Frontend API base URL (default: `http://localhost:8080/api`)

To customize, edit `docker-compose.yml` or create a `.env` file in the project root.

### Building Individual Images

**Backend only:**
```bash
cd backend
docker build -t maplewood-backend .
docker run -p 8080:8080 -v $(pwd)/../maplewood_school.sqlite:/app/maplewood_school.sqlite:ro maplewood-backend
```

**Frontend only:**
```bash
cd frontend
docker build -t maplewood-frontend .
docker run -p 3000:80 maplewood-frontend
```

## Configuration

### Backend Configuration

The backend configuration is in `backend/src/main/resources/application.yml`:
- Database connection
- Server port (default: 8080)
- Logging levels

### Frontend Configuration

The frontend API base URL can be configured via environment variable:
- Create `.env` file in `frontend/` directory:
  ```
  VITE_API_BASE_URL=http://localhost:8080/api
  ```

## Running Tests

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
(Not implemented in this version)

## Database Migrations

Flyway migrations are automatically applied on startup. Migration files are in:
`backend/src/main/resources/db/migration/`

The migrations create the following new tables:
- `course_sections` - Course section assignments
- `course_section_meetings` - Meeting times for sections
- `student_course_enrollments` - Student enrollments in sections

**Note:** The existing database tables (students, teachers, courses, etc.) are not modified by migrations.

## API Endpoints

### Admin Endpoints
- `POST /api/admin/schedules/generate` - Generate master schedule
- `GET /api/admin/schedules/{semesterId}` - Get schedule for semester
- `GET /api/admin/teachers/workload?semesterId={id}` - Get teacher workloads
- `GET /api/admin/rooms/usage?semesterId={id}` - Get room usage

### Student Endpoints
- `GET /api/students/{studentId}/plan?semesterId={id}` - Get student plan
- `POST /api/students/{studentId}/enroll` - Enroll student in courses
- `GET /api/students/{studentId}/progress` - Get academic progress

## Troubleshooting

### Docker Issues

1. **Port conflicts:**
   - Backend uses port 8080, frontend uses port 3000
   - Change ports in `docker-compose.yml` if needed:
     ```yaml
     ports:
       - "8081:8080"  # Backend
       - "3001:80"    # Frontend
     ```

2. **Database not found:**
   - Ensure `maplewood_school.sqlite` exists in the project root
   - Check volume mount in `docker-compose.yml`

3. **Build failures:**
   - Clear Docker cache: `docker system prune -a`
   - Rebuild: `docker-compose build --no-cache`

4. **Container won't start:**
   - Check logs: `docker-compose logs backend` or `docker-compose logs frontend`
   - Verify database file permissions

### Backend Issues

1. **Database connection errors:**
   - Verify `maplewood_school.sqlite` exists in the project root
   - Check the database path in `application.yml`

2. **Port already in use:**
   - Change the port in `application.yml`:
     ```yaml
     server:
       port: 8081
     ```

3. **Flyway migration errors:**
   - Check that the database is not locked
   - Verify migration files are in the correct directory

### Frontend Issues

1. **API connection errors:**
   - Verify backend is running on port 8080
   - Check CORS configuration in backend
   - Verify `VITE_API_BASE_URL` in `.env` file

2. **Build errors:**
   - Delete `node_modules` and `package-lock.json`, then run `npm install` again

## Development Notes

- The master schedule generation algorithm uses a greedy approach to assign teachers, rooms, and time slots
- Time constraints: School hours 9 AM - 5 PM, lunch break 12-1 PM, max 4 hours/day per teacher
- Students can enroll in up to 5 courses per semester
- Prerequisites are validated using `student_course_history` table

## Next Steps

1. Test the schedule generation with different semesters
2. Test student enrollment with various scenarios
3. Add more comprehensive error handling
4. Implement teacher workload and room usage statistics
5. Add unit and integration tests

