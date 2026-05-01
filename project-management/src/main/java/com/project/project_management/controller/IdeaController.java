package com.project.project_management.controller;

import com.project.project_management.dto.IdeaSubmitDTO;
import com.project.project_management.entity.Idea;
import com.project.project_management.service.IdeaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ideas")
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    // 🔥 SUBMIT IDEA
    @PostMapping("/submit")
    public Idea submitIdea(@RequestBody IdeaSubmitDTO dto) {

        return ideaService.submitIdea(
                dto.getClassId(),
                dto.getTitle(),
                dto.getDescription(),
                dto.getStudentIds()
        );
    }


    // 🔥 GET IDEAS
    @GetMapping("/class/{classId}")
    public List<Idea> getIdeas(@PathVariable Long classId) {
        return ideaService.getIdeasByClass(classId);
    }

    // 🔥 APPROVE (OLD)
    @PutMapping("/{id}/approve")
    public Idea approve(@PathVariable Long id) {
        return ideaService.approveIdea(id);
    }

    // 🔥 REJECT (OLD)
    @PutMapping("/{id}/reject")
    public Idea reject(@PathVariable Long id) {
        return ideaService.rejectIdea(id);
    }

    // 🔥 NEW: UPDATE STATUS (BEST FOR FRONTEND)
    @PutMapping("/update-status")
    public Idea updateStatus(@RequestParam Long ideaId,
                             @RequestParam String status) {

        if (status.equalsIgnoreCase("APPROVED")) {
            return ideaService.approveIdea(ideaId);
        } else if (status.equalsIgnoreCase("REJECTED")) {
            return ideaService.rejectIdea(ideaId);
        } else {
            throw new RuntimeException("Invalid status");
        }
    }
}

