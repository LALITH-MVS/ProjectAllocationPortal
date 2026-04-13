package com.project.project_management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ClassStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classes classes;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
}

