package com.example.discordbot.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_hours")  // Optional: Defines the table name explicitly
public class WorkHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String username;
    private LocalDateTime startHour;
    private LocalDateTime endHour;
    private Long hours;
    private LocalDateTime timestamp;

    public WorkHours() {}

    public WorkHours(String userId, String username, LocalDateTime startHour, LocalDateTime endHour, LocalDateTime timestamp) {
        this.userId = userId;
        this.username = username;
        this.startHour = startHour;
        this.endHour = endHour;
        this.hours = calculateHoursBetween();
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalDateTime startHour) {
        this.startHour = startHour;
    }

    public LocalDateTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalDateTime endHour) {
        this.endHour = endHour;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public long calculateHoursBetween() {
        if (startHour != null && endHour != null) {
            return Duration.between(startHour, endHour).toHours();
        }
        return 0;
    }
}
