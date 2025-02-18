package com.example.discordbot.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_hours")
public class WorkHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String username;
    private LocalDateTime shiftStart;  
    private LocalDateTime shiftEnd;    
    private Long minutes;
    private LocalDateTime timestamp;

    public WorkHours() {}

    public WorkHours(String userId, String username, LocalDateTime shiftStart, LocalDateTime shiftEnd, LocalDateTime timestamp) {
        this.userId = userId;
        this.username = username;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.minutes = calculateMinutesBetween();
        this.timestamp = timestamp;
    }

    public LocalDate getShiftDay() {
        return shiftStart.toLocalDate();
    }

    public int getShiftMonth() {
        return shiftStart.getMonthValue();
    }

    public int getShiftYear() {
        return shiftStart.getYear();
    }

    private long calculateMinutesBetween() {
        if (shiftStart != null && shiftEnd != null) {
            return Duration.between(shiftStart, shiftEnd).toMinutes();
        }
        return 0;
    }
}
