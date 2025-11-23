package com.maplewood.repository;

import com.maplewood.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
    @Query("SELECT c FROM Classroom c WHERE c.roomTypeId = :roomTypeId")
    List<Classroom> findByRoomTypeId(@Param("roomTypeId") Integer roomTypeId);
}

