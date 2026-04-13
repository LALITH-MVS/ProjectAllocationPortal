package com.project.project_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classId;

    @Column(unique = true, nullable = false)
    private String classCode;

    private String subjectName;

    // 🔥 FOREIGN KEY → USER TABLE
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;
}