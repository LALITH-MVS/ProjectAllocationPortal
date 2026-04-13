package com.project.project_management.service;

import com.project.project_management.entity.*;
import com.project.project_management.enums.IdeaStatus;
import com.project.project_management.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final IdeaMemberRepository ideaMemberRepository;
    private final ClassesRepository classesRepository;
    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    private final MailService mailService;
    private final AuditLogService auditLogService;
    public IdeaService(IdeaRepository ideaRepository,
                       IdeaMemberRepository ideaMemberRepository,
                       ClassesRepository classesRepository,
                       UserRepository userRepository,
                       ProjectRepository projectRepository,
                       TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository,
                       MailService mailService, AuditLogService auditLogService) {

        this.ideaRepository = ideaRepository;
        this.ideaMemberRepository = ideaMemberRepository;
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.mailService = mailService;
        this.auditLogService = auditLogService;
    }

    // 🔥 SUBMIT IDEA
    // 🔥 SUBMIT IDEA
    @Transactional
    public Idea submitIdea(Long classId, String title, String description, List<Long> studentIds) {

        Classes cls = classesRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        Idea idea = new Idea();
        idea.setTitle(title);
        idea.setDescription(description);
        idea.setStatus(IdeaStatus.PENDING);
        idea.setClasses(cls);

        Idea savedIdea = ideaRepository.save(idea);

        if (studentIds != null && !studentIds.isEmpty()) {
            for (Long studentId : studentIds) {
                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                IdeaMember member = new IdeaMember();
                member.setIdea(savedIdea);
                member.setStudent(student);

                ideaMemberRepository.save(member);
            }
        }

        // 🔥 NEW: GET STUDENT NAME (FIXED - moved outside try)
        String studentName = "Unknown";

        if (studentIds != null && !studentIds.isEmpty()) {
            studentName = userRepository.findById(studentIds.get(0))
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getName();
        }

        // 🔥 NEW: SEND EMAIL TO TEACHER
        try {
            String teacherEmail = cls.getTeacher().getEmail();

            mailService.sendIdeaSubmissionMail(
                    teacherEmail,
                    savedIdea.getTitle(),
                    studentName
            );

        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
        }

        // 🔥 NEW: AUDIT LOG
        auditLogService.log(
                "IDEA_SUBMITTED",
                studentName + " submitted idea '" + savedIdea.getTitle() + "'",
                cls
        );

        return savedIdea;
    }

    // 🔥 GET IDEAS
    public List<Idea> getIdeasByClass(Long classId) {
        return ideaRepository.findByClasses_ClassId(classId);
    }

    // 🔥 APPROVE IDEA → CREATE PROJECT + TEAM + MEMBERS
    @Transactional
    public Idea approveIdea(Long ideaId) {

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea not found"));

        // ❗ Prevent duplicate approval
        if (idea.getStatus() == IdeaStatus.APPROVED) {
            return idea;
        }

        // ✅ Update status
        idea.setStatus(IdeaStatus.APPROVED);

        auditLogService.log(
                "IDEA_APPROVED",
                "Teacher approved idea '" + idea.getTitle() + "'",
                idea.getClasses()
        );

        // 🔥 1. CREATE PROJECT
        Project project = new Project();
        project.setTitle(idea.getTitle());
        project.setDescription(idea.getDescription());
        project.setStatus("TAKEN");
        project.setClasses(idea.getClasses());

        Project savedProject = projectRepository.save(project);

        auditLogService.log(
                "TEAM_CREATED",
                "Team created for idea '" + idea.getTitle() + "'",
                idea.getClasses()
        );

        // 🔥 2. CREATE TEAM
        Team team = new Team();
        team.setTeamName("Team for " + idea.getTitle());
        team.setProject(savedProject);
        team.setMaxSize(3);

        Team savedTeam = teamRepository.save(team);
        auditLogService.log(
                "TEAM_CREATED",
                "Team created for idea '" + idea.getTitle() + "'",
                idea.getClasses()
        );
        // 🔥 ✅ ADD THIS LINE (ONLY CHANGE)
        idea.setTeam(savedTeam);

        // 🔥 3. COPY IDEA MEMBERS → TEAM MEMBERS
        List<IdeaMember> ideaMembers = ideaMemberRepository.findByIdea(idea);

        for (IdeaMember im : ideaMembers) {
            TeamMember tm = new TeamMember();
            tm.setTeam(savedTeam);
            tm.setStudent(im.getStudent());

            teamMemberRepository.save(tm);
        }
        auditLogService.log(
                "TEAM_CREATED",
                "Team created for idea '" + idea.getTitle() + "'",
                idea.getClasses()
        );
        return ideaRepository.save(idea);
    }

    // 🔥 REJECT IDEA
    public Idea rejectIdea(Long ideaId) {

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea not found"));

        idea.setStatus(IdeaStatus.REJECTED);

        auditLogService.log(
                "TEAM_MEMBERS_ADDED",
                "Members added to team for idea '" + idea.getTitle() + "'",
                idea.getClasses()
        );

        return ideaRepository.save(idea);
    }
}