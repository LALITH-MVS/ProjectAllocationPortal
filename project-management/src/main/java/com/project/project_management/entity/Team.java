package com.project.project_management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;

    // Optional: flexible size (you can keep or remove)
    private int maxSize = 3;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}