package com.project.project_management.entity;

import com.project.project_management.entity.Classes;
import com.project.project_management.enums.IdeaStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ideas")
@Data
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idea_id")
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private IdeaStatus status;

    // 🔗 Idea belongs to a Class
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Classes classes;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    // 🔥 Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Auto-manage timestamps
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}