package com.example.discordbot.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.discordbot.model.WorkHours;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WorkHoursRepository extends JpaRepository<WorkHours, Long> {

    @Query("SELECT SUM(minutes) FROM WorkHours w WHERE w.userId = :userId")
    Long getTotalMinutesWorked(@Param("userId") String userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM WorkHours w WHERE w.userId = :userId AND CAST(w.shiftStart AS DATE) = :date")
    int deleteByUserIdAndDate(@Param("userId") String userId, @Param("date") LocalDate date);

    @Transactional
    @Modifying
    @Query("DELETE FROM WorkHours w WHERE w.userId = :userId")
    int deleteAllByUserId(@Param("userId") String userId);

    @Query("SELECT SUM(minutes) " +
            "FROM WorkHours w WHERE w.userId = :userId " +
            "AND EXTRACT(MONTH FROM w.shiftStart) = :month " +
            "AND EXTRACT(YEAR FROM w.shiftStart) = :year")
    Long getTotalMinutesWorkedByMonth(@Param("userId") String userId,
            @Param("month") int month,
            @Param("year") int year);

}