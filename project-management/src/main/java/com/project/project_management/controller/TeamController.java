package com.project.project_management.controller;

import com.project.project_management.entity.Project;
import com.project.project_management.entity.Team;
import com.project.project_management.entity.TeamMember;
import com.project.project_management.repository.ProjectRepository;
import com.project.project_management.repository.TeamRepository;
import com.project.project_management.service.TeamMemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamController {

    private final TeamMemberService teamMemberService;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;

    public TeamController(TeamMemberService teamMemberService,
                          TeamRepository teamRepository,
                          ProjectRepository projectRepository) {
        this.teamMemberService = teamMemberService;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
    }

    // ✅ CREATE TEAM (with project)
    @PostMapping("/create")
    public Team createTeam(@RequestParam Long projectId,
                           @RequestBody Team team) {

        Project project = projectRepository.findById(projectId).orElse(null);

        if (project == null) {
            throw new RuntimeException("Project not found");
        }

        team.setProject(project);

        return teamRepository.save(team);
    }

    // 🔥 ADD MEMBER TO TEAM
    @PostMapping("/add-member")
    public String addMember(@RequestParam Long teamId,
                            @RequestParam Long userId) {

        return teamMemberService.addMember(teamId, userId);
    }

    // ✅ GET ALL MEMBERS OF A TEAM
    @GetMapping("/{teamId}/members")
    public List<TeamMember> getMembers(@PathVariable Long teamId) {
        return teamMemberService.getMembers(teamId);
    }

    // 🔥 REMOVE MEMBER FROM TEAM
    @DeleteMapping("/remove-member")
    public String removeMember(@RequestParam Long teamId,
                               @RequestParam Long userId) {

        return teamMemberService.removeMember(teamId, userId);
    }

    // ✅ GET ALL TEAMS (useful for frontend)
    @GetMapping("/all")
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
}