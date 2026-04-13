package com.project.project_management.controller;

import com.project.project_management.entity.Classes;
import com.project.project_management.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes")
public class ClassesController {

    @Autowired
    private ClassesService classesService;

    @PostMapping("/create")
    public Classes createClass(@RequestBody Classes classes) {
        return classesService.createClass(classes);
    }

    @DeleteMapping("/delete")
    public String deleteClass(@RequestParam Long classId,
                              @RequestParam Long teacherId) {

        classesService.deleteClass(classId, teacherId);
        return "Class deleted successfully";
    }
}