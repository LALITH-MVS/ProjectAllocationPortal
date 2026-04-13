package com.project.project_management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
}