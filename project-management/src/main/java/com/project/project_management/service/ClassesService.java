package com.project.project_management.service;

import com.project.project_management.dto.ClassDashboardDTO;
import com.project.project_management.dto.IdeaCardDTO;
import com.project.project_management.dto.ProjectCardDTO;
import com.project.project_management.entity.*;
import com.project.project_management.enums.IdeaStatus;
import com.project.project_management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.project.project_management.dto.TeacherClassDTO;

import java.util.ArrayList;
import java.util.List;
@Service
public class ClassesService {

    private final ClassesRepository classesRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;


    @Autowired
    private ClassStudentRepository classStudentRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private IdeaMemberRepository ideaMemberRepository;
    public ClassesService(ClassesRepository classesRepository,
                          UserRepository userRepository,
                          AuditLogService auditLogService) {
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }


    public Classes createClass(Classes classes) {

        // 🔥 Get logged-in user email from JWT
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 🔥 Fetch user from DB
        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 ROLE CHECK (extra safety)
        if (!teacher.getRole().equals("TEACHER")) {
            throw new RuntimeException("Only teachers can create class");
        }

        // 🔥 assign teacher automatically
        classes.setTeacher(teacher);

        // duplicate check
        if (classesRepository.findByClassCode(classes.getClassCode()).isPresent()) {
            throw new RuntimeException("Class code already exists!");
        }

        Classes savedClass = classesRepository.save(classes);

        // audit log
        auditLogService.log(
                "CLASS_CREATED",
                "Teacher created class " + savedClass.getSubjectName(),
                savedClass.getClassId()
        );

        return savedClass;
    }

    public void deleteClass(Long classId, Long teacherId) {

        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // 🔥 check ownership
        if (!classes.getTeacher().getUserId().equals(teacherId)) {
            throw new RuntimeException("You are not allowed to delete this class");
        }

        String subjectName = classes.getSubjectName();

        classesRepository.delete(classes);

        auditLogService.log(
                "CLASS_DELETED",
                "Teacher deleted class " + subjectName,
                classId
        );
    }

    public List<TeacherClassDTO> getMyClasses() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!teacher.getRole().equals("TEACHER")) {
            throw new RuntimeException("Only teachers allowed");
        }

        List<Classes> classesList = classesRepository.findByTeacher(teacher);

        List<TeacherClassDTO> result = new ArrayList<>();

        for (Classes c : classesList) {

            int studentCount =
                    classStudentRepository.findByClasses_ClassId(c.getClassId()).size();

            result.add(new TeacherClassDTO(
                    c.getClassId(),
                    c.getSubjectName(),
                    c.getClassCode(),
                    studentCount,
                    teacher.getName(),        // ✅ teacher name
                    teacher.getRegNo()        // ✅ teacher code
            ));
        }

        return result;
    }

    public ClassDashboardDTO getClassDashboard(Long classId) {

        // ✅ GET CLASS
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // ✅ STUDENT COUNT
        int studentCount =
                classStudentRepository.findByClasses_ClassId(classId).size();

        // ✅ PROJECTS
        List<Project> projects = projectRepository.findByClasses(classes);

        // ✅ TEAMS
        List<Team> teams = projects.stream()
                .map(p -> teamRepository.findByProject(p))
                .filter(t -> t != null)
                .toList();

        // ✅ IDEAS (ONLY PENDING)
        List<Idea> ideas = ideaRepository.findByClasses_ClassId(classId)
                .stream()
                .filter(i -> i.getStatus() == IdeaStatus.PENDING)
                .toList();

        int pendingIdeas = ideas.size();

        // ==============================
        // ✅ SPLIT PROJECTS
        // ==============================
        List<ProjectCardDTO> selectedProjects = new ArrayList<>();
        List<ProjectCardDTO> availableProjects = new ArrayList<>();

        for (Project p : projects) {

            Team team = teamRepository.findByProject(p);

            if (team != null) {
                // ✅ SELECTED
                List<String> members = teamMemberRepository.findByTeamId(team.getId())
                        .stream()
                        .map(tm -> tm.getStudent().getName())
                        .toList();

                selectedProjects.add(new ProjectCardDTO(
                        p.getTitle(),
                        members
                ));

            } else {
                // ✅ AVAILABLE
                availableProjects.add(new ProjectCardDTO(
                        p.getTitle(),
                        List.of()
                ));
            }
        }

        // ==============================
        // ✅ IDEA DTO
        // ==============================
        List<IdeaCardDTO> ideaDTOs = ideas.stream().map(i -> {

            List<String> members = ideaMemberRepository.findByIdea(i)
                    .stream()
                    .map(im -> im.getStudent().getName())
                    .toList();

            return new IdeaCardDTO(
                    i.getId(),
                    i.getTitle(),
                    i.getDescription(),
                    members
            );

        }).toList();

        // ==============================
        // ✅ FINAL RESPONSE
        // ==============================
        return new ClassDashboardDTO(
                studentCount,
                projects.size(),
                teams.size(),
                pendingIdeas,
                selectedProjects,
                availableProjects,
                ideaDTOs
        );
    }
}

