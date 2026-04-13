package com.project.project_management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "idea_members")
@Data
public class IdeaMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Link to Idea
    @ManyToOne
    @JoinColumn(name = "idea_id", nullable = false)
    private Idea idea;

    // 🔗 Link to Student (User)
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
}