package com.project.project_management.dto;

public class StudentClassDTO {

    private Long classId;
    private String className;
    private String classCode;
    private String teacherName;

    public StudentClassDTO(Long classId, String className, String classCode, String teacherName) {
        this.classId = classId;
        this.className = className;
        this.classCode = classCode;
        this.teacherName = teacherName;
    }

    public Long getClassId() { return classId; }
    public String getClassName() { return className; }
    public String getClassCode() { return classCode; }
    public String getTeacherName() { return teacherName; }
}