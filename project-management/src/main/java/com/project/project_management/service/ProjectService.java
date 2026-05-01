package com.project.project_management.service;

import com.project.project_management.dto.AvailableProjectDTO;
import com.project.project_management.dto.SelectedProjectDTO;
import com.project.project_management.entity.*;
import com.project.project_management.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private ClassStudentRepository classStudentRepository;

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
                savedProject.getClasses().getClassId()
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
                project.getClasses().getClassId()
        );
        return "Project deleted successfully";
    }

    // 🔥🔥 CORE FEATURE: PROJECT SELECTION (TRANSACTION)
    @Transactional
    public String selectProject(Long projectId, List<Long> memberIds) {

        // 🔐 1. Logged-in user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getRole().equals("STUDENT")) {
            throw new RuntimeException("Only students can select project");
        }

        // 🔍 2. Get project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getStatus().equals("AVAILABLE")) {
            throw new RuntimeException("Project already taken");
        }

        // 🔥 3. CHECK: current user must be in class
        if (!classStudentRepository.existsByClassesAndStudent(project.getClasses(), currentUser)) {
            throw new RuntimeException("You are not part of this class");
        }

        // ❗ Prevent user already in team
        if (teamMemberRepository.existsByStudent(currentUser)) {
            throw new RuntimeException("You are already in a team");
        }

        // 🔥 4. Create team
        Team team = new Team();
        team.setProject(project);
        team.setTeamName("Team-" + projectId);
        teamRepository.save(team);

        int teamSize = 1; // current user already added

        // 🔥 5. ADD CURRENT USER
        TeamMember self = new TeamMember();
        self.setTeam(team);
        self.setStudent(currentUser);
        teamMemberRepository.save(self);

        // 🔥 6. Add other members
        for (Long id : memberIds) {

            if (id == null) continue;   // ✅ SKIP EMPTY INPUTS
            // ❗ skip self
            if (id.equals(currentUser.getUserId())) continue;

            User student = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            if (!student.getRole().equals("STUDENT")) {
                throw new RuntimeException("Only students allowed");
            }

            // 🔥 CLASS VALIDATION
            if (!classStudentRepository.existsByClassesAndStudent(project.getClasses(), student)) {
                throw new RuntimeException("Student " + student.getName() + " is not in this class");
            }

            // ❗ Already in another team
            if (teamMemberRepository.existsByStudent(student)) {
                throw new RuntimeException("Student already in another team");
            }

            // 🔥 TEAM SIZE CHECK (optional but recommended)
            if (teamSize >= team.getMaxSize()) {
                throw new RuntimeException("Team is full (max " + team.getMaxSize() + ")");
            }

            TeamMember member = new TeamMember();
            member.setTeam(team);
            member.setStudent(student);
            teamMemberRepository.save(member);

            teamSize++;
        }

        // 🔥 7. Update project
        project.setStatus("TAKEN");
        projectRepository.save(project);

        // 🔥 8. Audit log (CLASS ID ✔)
        auditLogService.log(
                "PROJECT_SELECTED",
                currentUser.getName() + " created team for project '" + project.getTitle() + "'",
                project.getClasses().getClassId()
        );

        return "Project selected successfully";
    }

    @Transactional
    public String leaveProject(Long teamId) {

        // 🔐 1. Get logged-in user
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❗ Role check
        if (!currentUser.getRole().equals("STUDENT")) {
            throw new RuntimeException("Only students can leave project");
        }

        // 🔍 2. Get team
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        Project project = team.getProject();

        // ❗ 3. Check if user is part of this team
        TeamMember member = teamMemberRepository
                .findByTeamAndStudent(team, currentUser);

        if (member == null) {
            throw new RuntimeException("You are not part of this team");
        }

        // 🔥 4. Delete all members
        teamMemberRepository.deleteByTeam(team);

        // 🔥 5. Delete team
        teamRepository.delete(team);

        // 🔥 6. Make project available again
        project.setStatus("AVAILABLE");
        projectRepository.save(project);

        // 🔥 7. Audit log (ONLY ONE LOG IS ENOUGH)
        auditLogService.log(
                "TEAM_LEFT_PROJECT",
                currentUser.getName() + " left team '" + team.getTeamName() +
                        "' for project '" + project.getTitle() + "'",
                project.getClasses().getClassId()
        );

        return "Team removed and project is now available";
    }


    //for frontend purpose
    public List<SelectedProjectDTO> getSelectedProjects(Long classId) {

        List<Project> projects = projectRepository.findByClasses_ClassIdAndStatus(classId, "TAKEN");

        List<SelectedProjectDTO> result = new ArrayList<>();

        for (Project project : projects) {

            Team team = teamRepository.findByProject(project);

            if (team == null) continue;

            List<TeamMember> members = teamMemberRepository.findByTeamId(team.getId());

            List<String> names = new ArrayList<>();

            for (TeamMember tm : members) {
                if (tm.getStudent() != null) {
                    names.add(tm.getStudent().getName());
                }
            }

            result.add(new SelectedProjectDTO(project.getTitle(), names));
        }

        return result;
    }


    //for frontend purpose
    public List<AvailableProjectDTO> getAvailableProjects(Long classId) {

        List<Project> projects = projectRepository.findByClasses_ClassIdAndStatus(classId, "AVAILABLE");

        List<AvailableProjectDTO> result = new ArrayList<>();

        for (Project p : projects) {
            result.add(new AvailableProjectDTO(
                    p.getProjectId(),
                    p.getTitle(),
                    p.getDescription()
            ));
        }

        return result;
    }
}

