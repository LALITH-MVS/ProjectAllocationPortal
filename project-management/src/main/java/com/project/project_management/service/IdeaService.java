package com.project.project_management.service;

import com.project.project_management.entity.*;
import com.project.project_management.enums.IdeaStatus;
import com.project.project_management.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.project_management.repository.ClassStudentRepository;
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
    private final ClassStudentRepository classStudentRepository;
    private final MailService mailService;
    private final AuditLogService auditLogService;
    public IdeaService(IdeaRepository ideaRepository,
                       IdeaMemberRepository ideaMemberRepository,
                       ClassesRepository classesRepository,
                       UserRepository userRepository,
                       ProjectRepository projectRepository,
                       TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository,
                       ClassStudentRepository classStudentRepository,
                       MailService mailService, AuditLogService auditLogService) {

        this.ideaRepository = ideaRepository;
        this.ideaMemberRepository = ideaMemberRepository;
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.mailService = mailService;
        this.classStudentRepository = classStudentRepository;
        this.auditLogService = auditLogService;
    }

    // 🔥 SUBMIT IDEA
    // 🔥 SUBMIT IDEA
    @Transactional
    public Idea submitIdea(Long classId, String title, String description, List<Long> studentIds) {

        // 🔐 NEW: Logged-in user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❗ NEW: Role check
        if (!currentUser.getRole().equals("STUDENT")) {
            throw new RuntimeException("Only students can submit ideas");
        }

        Classes cls = classesRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // ❗ NEW: Must belong to class
        if (!classStudentRepository.existsByClassesAndStudent(cls, currentUser)) {
            throw new RuntimeException("You are not part of this class");
        }

        Idea idea = new Idea();
        idea.setTitle(title);
        idea.setDescription(description);
        idea.setStatus(IdeaStatus.PENDING);
        idea.setClasses(cls);

        Idea savedIdea = ideaRepository.save(idea);

        // 🔥 NEW: Always add current user
        IdeaMember self = new IdeaMember();
        self.setIdea(savedIdea);
        self.setStudent(currentUser);
        ideaMemberRepository.save(self);

        if (studentIds != null && !studentIds.isEmpty()) {
            for (Long studentId : studentIds) {

                // ❗ skip self duplicate
                if (studentId.equals(currentUser.getUserId())) continue;

                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // ❗ NEW: role check
                if (!student.getRole().equals("STUDENT")) {
                    throw new RuntimeException("Only students allowed");
                }

                // ❗ NEW: must belong to same class
                if (!classStudentRepository.existsByClassesAndStudent(cls, student)) {
                    throw new RuntimeException("Student not in this class");
                }

                IdeaMember member = new IdeaMember();
                member.setIdea(savedIdea);
                member.setStudent(student);

                ideaMemberRepository.save(member);
            }
        }

        // 🔥 KEEP YOUR EXISTING EMAIL LOGIC (UNCHANGED)
        String studentName = currentUser.getName();

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

        // 🔥 KEEP YOUR AUDIT LOG (UNCHANGED)
        auditLogService.log(
                "IDEA_SUBMITTED",
                studentName + " submitted idea '" + savedIdea.getTitle() + "'",
                cls.getClassId()
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

        // 🔐 Get logged-in user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❗ Only teacher allowed
        if (!currentUser.getRole().equals("TEACHER")) {
            throw new RuntimeException("Only teachers can approve ideas");
        }

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
                idea.getClasses().getClassId()
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
                idea.getClasses().getClassId()
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
                idea.getClasses().getClassId()
        );

        // 🔥 LINK TEAM TO IDEA
        idea.setTeam(savedTeam);

        // 🔥 3. ADD CURRENT USER (EXTRA SAFETY FIX)
        // (even though teacher is approving, ensures consistency if needed)
        if (!teamMemberRepository.existsByTeamAndStudent(savedTeam, currentUser)) {
            // ❗ Usually teacher is not added, but safe check kept
            // You can remove this if you NEVER want teacher in team
        }

        // 🔥 4. COPY IDEA MEMBERS → TEAM MEMBERS (WITH DUPLICATE CHECK)
        List<IdeaMember> ideaMembers = ideaMemberRepository.findByIdea(idea);

        for (IdeaMember im : ideaMembers) {

            if (!teamMemberRepository.existsByTeamAndStudent(savedTeam, im.getStudent())) {

                TeamMember tm = new TeamMember();
                tm.setTeam(savedTeam);
                tm.setStudent(im.getStudent());

                teamMemberRepository.save(tm);
            }
        }

        auditLogService.log(
                "TEAM_CREATED",
                "Team created for idea '" + idea.getTitle() + "'",
                idea.getClasses().getClassId()
        );

        return ideaRepository.save(idea);
    }

    // 🔥 REJECT IDEA
    public Idea rejectIdea(Long ideaId) {

        // 🔐 NEW: Get logged-in user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❗ NEW: Only teacher allowed
        if (!currentUser.getRole().equals("TEACHER")) {
            throw new RuntimeException("Only teachers can reject ideas");
        }

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea not found"));

        idea.setStatus(IdeaStatus.REJECTED);

        auditLogService.log(
                "IDEA_REJECTED",   // 🔥 small fix (better naming)
                "Teacher rejected idea '" + idea.getTitle() + "'",
                idea.getClasses().getClassId()
        );

        return ideaRepository.save(idea);
    }
}

