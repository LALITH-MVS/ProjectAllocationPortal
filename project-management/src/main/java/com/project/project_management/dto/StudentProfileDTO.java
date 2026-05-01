package com.project.project_management.dto;

public class StudentProfileDTO {

    private String name;
    private String regNo;

    public StudentProfileDTO(String name, String regNo) {
        this.name = name;
        this.regNo = regNo;
    }

    public String getName() { return name; }
    public String getRegNo() { return regNo; }
}