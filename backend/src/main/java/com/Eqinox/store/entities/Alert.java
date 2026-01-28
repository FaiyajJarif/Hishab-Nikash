package com.Eqinox.store.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;
    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(length = 20)
    private String severity;

    public Alert() {} // ✅ required by JPA

    public Alert(Integer userId, String message) { // ✅ easy creation
        this.userId = userId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Alert(Integer userId, String message, String severity) {
        this.userId = userId;
        this.message = message;
        this.severity = severity;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } // optional

    public String getSeverity() {
        return severity;
    }
    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
