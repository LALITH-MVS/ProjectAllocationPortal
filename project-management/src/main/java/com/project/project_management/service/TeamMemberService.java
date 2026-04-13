package com.project.project_management.service;

import com.project.project_management.entity.Team;
import com.project.project_management.entity.TeamMember;
import com.project.project_management.entity.User;
import com.project.project_management.repository.TeamMemberRepository;
import com.project.project_management.repository.TeamRepository;
import com.project.project_management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamMemberService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TeamMemberService(TeamRepository teamRepository,
                             UserRepository userRepository,
                             TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    // 🔥 ADD MEMBER
    public String addMember(Long teamId, Long userId) {

        Team team = teamRepository.findById(teamId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (team == null || user == null) {
            return "Invalid team or user";
        }

        // 🔥 ONE USER = ONE TEAM (NEW)
        if (teamMemberRepository.existsByStudent(user)) {
            return "User already in another team";
        }

        // 🔥 TEAM SIZE CHECK
        int count = teamMemberRepository.countByTeam(team);

        if (count >= team.getMaxSize()) {
            return "Team is full (max " + team.getMaxSize() + " members)";
        }

        // 🔥 DUPLICATE CHECK
        if (teamMemberRepository.existsByTeamAndStudent(team, user)) {
            return "User already in team";
        }

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setStudent(user);

        teamMemberRepository.save(member);

        return "Member added successfully";
    }

    // ✅ GET MEMBERS OF TEAM
    public List<TeamMember> getMembers(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    // 🔥 REMOVE MEMBER
    public String removeMember(Long teamId, Long userId) {

        Team team = teamRepository.findById(teamId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (team == null || user == null) {
            return "Invalid team or user";
        }

        TeamMember member = teamMemberRepository
                .findByTeamAndStudent(team, user);

        if (member != null) {
            teamMemberRepository.delete(member);
            return "Removed successfully";
        }

        return "Member not found";
    }
}