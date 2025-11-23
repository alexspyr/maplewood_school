package com.maplewood.repository;

import com.maplewood.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    @Query("SELECT s FROM Semester s WHERE s.name = :name AND s.year = :year")
    Optional<Semester> findByNameAndYear(@Param("name") String name, @Param("year") Integer year);
}

