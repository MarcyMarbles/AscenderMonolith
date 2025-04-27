package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.Tournament;
import kz.saya.project.ascender.Entities.TournamentMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, UUID> {
    List<TournamentMatch> findByTournament(Tournament tournament);
    List<TournamentMatch> findByTournamentOrderByRoundAscMatchNumberAsc(Tournament tournament);
    List<TournamentMatch> findByTournamentAndRound(Tournament tournament, Integer round);
}