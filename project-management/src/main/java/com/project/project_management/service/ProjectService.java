package com.project.project_management.service;

import com.project.project_management.entity.*;
import com.project.project_management.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    // ✅ Teacher adds project
    public String addProject(Long classId, String title, String description) {

        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setStatus("AVAILABLE");
        project.setClasses(classes);

        projectRepository.save(project);
        Project savedProject = projectRepository.save(project);

        auditLogService.log(
                "PROJECT_ADDED",
                "Teacher added project '" + savedProject.getTitle() + "'",
                savedProject.getClasses()
        );
        return "Project added successfully";
    }

    // ✅ Student views projects of a class
    public List<Project> getProjectsByClass(Long classId) {

        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        return projectRepository.findByClasses(classes);
    }

    // ✅ Teacher deletes project
    public String deleteProject(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project.getStatus().equals("TAKEN")) {
            return "Cannot delete project. It is already taken by a team";
        }

        projectRepository.delete(project);
        auditLogService.log(
                "PROJECT_DELETED",
                "Teacher deleted project '" + project.getTitle() + "'",
                project.getClasses()
        );
        return "Project deleted successfully";
    }

    // 🔥🔥 CORE FEATURE: PROJECT SELECTION (TRANSACTION)
    @Transactional
    public String selectProject(Long projectId, List<Long> studentIds) {

        // 🔥 ADD HERE
        System.out.println("Selecting project: " + projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // ❗ Check if project already taken
        if (!project.getStatus().equals("AVAILABLE")) {
            throw new RuntimeException("Project already taken");
        }

        // 1️⃣ Create Team
        Team team = new Team();
        team.setProject(project);
        team.setTeamName("Team-" + projectId); // optional naming
        teamRepository.save(team);

        // 2️⃣ Add Members
        for (Long studentId : studentIds) {

            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // ❗ Prevent student joining multiple teams
            if (teamMemberRepository.existsByStudent(student)) {
                throw new RuntimeException("Student already in another team");
            }

            TeamMember member = new TeamMember();
            member.setTeam(team);
            member.setStudent(student);

            teamMemberRepository.save(member);
        }

        // 3️⃣ Update Project Status
        project.setStatus("TAKEN");
        projectRepository.save(project);

        // 🔥 ✅ ADD AUDIT LOG HERE (CORRECT PLACE)
        auditLogService.log(
                "TEAM_SELECTED_PROJECT",
                "Team '" + team.getTeamName() + "' selected project '" + project.getTitle() + "'",
                project.getClasses()
        );

        return "Project selected successfully";
    }

    @Transactional
    public String leaveProject(Long teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        Project project = team.getProject();

        // delete members
        teamMemberRepository.deleteByTeam(team);

        // delete team
        teamRepository.delete(team);

        // update project
        project.setStatus("AVAILABLE");
        projectRepository.save(project);

        // audit log
        auditLogService.log(
                "TEAM_LEFT_PROJECT",
                "Team '" + team.getTeamName() + "' left project '" + project.getTitle() + "'",
                project.getClasses()
        );
        auditLogService.log(
                "TEAM_LEFT_PROJECT",
                "Team '" + team.getTeamName() + "' left project '" + project.getTitle() + "'. Project is now available for others",
                project.getClasses()
        );
        return "Team removed and project is now available";
    }
}