package com.example.discordbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.discordbot.model.WorkHours;

@Repository
public interface WorkHoursRepository extends JpaRepository<WorkHours, Long> {
    @Query("SELECT SUM(TIMESTAMPDIFF(MINUTE, w.startHour, w.endHour)) FROM WorkHours w WHERE w.userId = :userId")
    Long getTotalMinutesWorked(@Param("userId") String userId);
}

