#!/bin/bash

# Script to verify the database is properly configured for Docker

echo "🔍 Verifying Database Configuration..."
echo ""

# Check if database file exists
if [ ! -f "maplewood_school.sqlite" ]; then
    echo "❌ ERROR: maplewood_school.sqlite not found in project root!"
    exit 1
fi

echo "✅ Database file found: maplewood_school.sqlite"

# Check database size
DB_SIZE=$(du -h maplewood_school.sqlite | cut -f1)
echo "📊 Database size: $DB_SIZE"

# Check if database has data
STUDENT_COUNT=$(sqlite3 maplewood_school.sqlite "SELECT COUNT(*) FROM students;" 2>/dev/null)
TEACHER_COUNT=$(sqlite3 maplewood_school.sqlite "SELECT COUNT(*) FROM teachers;" 2>/dev/null)
COURSE_COUNT=$(sqlite3 maplewood_school.sqlite "SELECT COUNT(*) FROM courses;" 2>/dev/null)

if [ -z "$STUDENT_COUNT" ] || [ "$STUDENT_COUNT" -eq 0 ]; then
    echo "❌ ERROR: Database appears to be empty or corrupted!"
    exit 1
fi

echo "✅ Database contains pre-populated data:"
echo "   - Students: $STUDENT_COUNT"
echo "   - Teachers: $TEACHER_COUNT"
echo "   - Courses: $COURSE_COUNT"
echo ""

# Check docker-compose.yml
if grep -q "maplewood_school.sqlite:/app/maplewood_school.sqlite" docker-compose.yml; then
    echo "✅ Docker volume mount configured correctly"
else
    echo "❌ ERROR: Docker volume mount not found in docker-compose.yml"
    exit 1
fi

# Check environment variable
if grep -q "SPRING_DATASOURCE_URL=jdbc:sqlite:/app/maplewood_school.sqlite" docker-compose.yml; then
    echo "✅ Database URL environment variable configured correctly"
else
    echo "❌ ERROR: Database URL not configured in docker-compose.yml"
    exit 1
fi

echo ""
echo "✅ All checks passed! Docker is configured to use the pre-populated database."
echo ""
echo "📝 Note: The database is mounted as a volume, so:"
echo "   - Changes made in Docker will persist to the host file"
echo "   - Flyway migrations will update the database"
echo "   - The pre-populated data will be preserved"

