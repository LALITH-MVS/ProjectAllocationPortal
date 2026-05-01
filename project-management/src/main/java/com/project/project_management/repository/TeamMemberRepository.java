package com.project.project_management.repository;

import com.project.project_management.entity.Team;
import com.project.project_management.entity.TeamMember;
import com.project.project_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    int countByTeam(Team team);

    boolean existsByTeamAndStudent(Team team, User student);

    boolean existsByStudent(User student); // 🔥 for one-team restriction

    List<TeamMember> findByTeamId(Long teamId);

    TeamMember findByTeamAndStudent(Team team, User student); // 🔥 needed
    void deleteByTeam(Team team);

    List<TeamMember> findByStudent(User student);
}