package com.project.project_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeacherClassDTO {

    private Long classId;
    private String subjectName;
    private String classCode;

    private int studentCount;

    // ✅ NEW
    private String teacherName;
    private String teacherCode;
}

//this is the dashboard of teacher when he login the page
