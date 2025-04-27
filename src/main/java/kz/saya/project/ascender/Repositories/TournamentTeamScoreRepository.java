package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Entities.TournamentMatch;
import kz.saya.project.ascender.Entities.TournamentTeamScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TournamentTeamScoreRepository extends JpaRepository<TournamentTeamScore, UUID> {
    List<TournamentTeamScore> findByMatch(TournamentMatch match);
    List<TournamentTeamScore> findByMatchOrderByScoreDesc(TournamentMatch match);
    List<TournamentTeamScore> findByMatchOrderByPositionAsc(TournamentMatch match);
    Optional<TournamentTeamScore> findByMatchAndTeam(TournamentMatch match, Team team);
}