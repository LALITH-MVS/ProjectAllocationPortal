package com.project.project_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String title;
    private String description;

    // AVAILABLE / TAKEN
    private String status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "class_id")
    private Classes classes;
}