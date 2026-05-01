package com.project.project_management.service;

import com.project.project_management.dto.StudentClassDTO;
import com.project.project_management.dto.StudentIdeaDTO;
import com.project.project_management.dto.StudentProfileDTO;
import com.project.project_management.dto.StudentProjectDTO;
import com.project.project_management.entity.*;
import com.project.project_management.repository.ClassStudentRepository;
import com.project.project_management.repository.IdeaMemberRepository;
import com.project.project_management.repository.TeamMemberRepository;
import com.project.project_management.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final ClassStudentRepository classStudentRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final IdeaMemberRepository ideaMemberRepository;

    public StudentService(UserRepository userRepository,
                          ClassStudentRepository classStudentRepository,
                          TeamMemberRepository teamMemberRepository,
                          IdeaMemberRepository ideaMemberRepository) {
        this.userRepository = userRepository;
        this.classStudentRepository = classStudentRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.ideaMemberRepository = ideaMemberRepository;
    }

    // 🔹 PROFILE
    public StudentProfileDTO getProfile() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new StudentProfileDTO(user.getName(), user.getRegNo());
    }

    // 🔹 CLASSES
    public List<StudentClassDTO> getMyClasses() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ClassStudent> mappings = classStudentRepository.findByStudent(user);

        List<StudentClassDTO> result = new ArrayList<>();

        for (ClassStudent cs : mappings) {

            Classes cls = cs.getClasses();
            User teacher = cls.getTeacher();

            result.add(new StudentClassDTO(
                    cls.getClassId(),
                    cls.getSubjectName(),
                    cls.getClassCode(),
                    teacher.getName()
            ));
        }

        return result;
    }

    // 🔹 PROJECTS
    public List<StudentProjectDTO> getMyProjects() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TeamMember> myTeams = teamMemberRepository.findByStudent(user);

        List<StudentProjectDTO> result = new ArrayList<>();

        for (TeamMember tm : myTeams) {

            Team team = tm.getTeam();
            Project project = team.getProject();

            // 🔥 Get all teammates
            List<TeamMember> teamMembers = teamMemberRepository.findByTeamId(team.getId());

            List<String> names = new ArrayList<>();

            for (TeamMember member : teamMembers) {
                names.add(member.getStudent().getName());
            }

            result.add(new StudentProjectDTO(
                    project.getTitle(),
                    names
            ));
        }

        return result;
    }
    // 🔹 IDEAS
    public List<StudentIdeaDTO> getMyIdeas() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<IdeaMember> ideaMembers = ideaMemberRepository.findByStudent(user);

        List<StudentIdeaDTO> result = new ArrayList<>();

        for (IdeaMember im : ideaMembers) {

            Idea idea = im.getIdea();

            result.add(new StudentIdeaDTO(
                    idea.getTitle(),
                    idea.getStatus().name()
            ));
        }

        return result;
    }
}
