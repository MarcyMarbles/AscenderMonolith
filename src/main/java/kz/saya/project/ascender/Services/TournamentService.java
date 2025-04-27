package kz.saya.project.ascender.Services;

import kz.saya.project.ascender.Entities.*;
import kz.saya.project.ascender.Enums.TechResult;
import kz.saya.project.ascender.Repositories.TournamentMatchRepository;
import kz.saya.project.ascender.Repositories.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMatchRepository tournamentMatchRepository;
    private final TeamService teamService;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository,
                             TournamentMatchRepository tournamentMatchRepository, TeamService teamService) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentMatchRepository = tournamentMatchRepository;
        this.teamService = teamService;
    }
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Optional<Tournament> getTournamentById(UUID id) {
        return tournamentRepository.findById(id);
    }

    @Transactional
    public Tournament saveTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public void deleteTournament(UUID id) {
        tournamentRepository.deleteById(id);
    }

    // Tournament Team Management
    @Transactional
    public Tournament addTeamToTournament(UUID tournamentId, Team team) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        if (tournament.getMaxTeams() != null &&
                tournament.getTeams().size() >= tournament.getMaxTeams()) {
            throw new IllegalStateException("Tournament has reached maximum number of teams");
        }

        tournament.getTeams().add(team);
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public Tournament addTeamToTournament(UUID tournamentId, UUID teamId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        if (tournament.getMaxTeams() != null &&
                tournament.getTeams().size() >= tournament.getMaxTeams()) {
            throw new IllegalStateException("Tournament has reached maximum number of teams");
        }

        Team team = teamService.getTeamById(teamId).orElseThrow(() ->
                new NoSuchElementException("Team not found"));

        tournament.getTeams().add(team);
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public Tournament removeTeamFromTournament(UUID tournamentId, UUID teamId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        tournament.setTeams(tournament.getTeams().stream()
                .filter(team -> !team.getId().equals(teamId))
                .collect(Collectors.toSet()));

        return tournamentRepository.save(tournament);
    }
    @Transactional
    public TournamentMatch createMatch(UUID tournamentId, List<UUID> teamIds, Integer round, String matchNumber) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        TournamentMatch match = new TournamentMatch();
        match.setTournament(tournament);
        match.setRound(round);
        match.setMatchNumber(matchNumber);

        match = tournamentMatchRepository.save(match);

        for (UUID teamId : teamIds) {
            Team team = tournament.getTeams().stream()
                    .filter(t -> t.getId().equals(teamId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Team not found in tournament: " + teamId));

            match.addTeam(team);
        }

        return tournamentMatchRepository.save(match);
    }


    @Transactional
    public TournamentMatch updateMatchScore(UUID matchId, Map<UUID, Integer> teamScores) {
        TournamentMatch match = tournamentMatchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));

        for (Map.Entry<UUID, Integer> entry : teamScores.entrySet()) {
            UUID teamId = entry.getKey();
            Integer score = entry.getValue();

            Team team = match.getTeamScores().stream()
                    .map(TournamentTeamScore::getTeam)
                    .filter(t -> t.getId().equals(teamId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Team not found in match: " + teamId));

            match.updateScore(team, score);
        }

        if (match.getTeamScores().stream().allMatch(ts -> ts.getScore() != null)) {
            match.setStatus(TournamentMatch.MatchStatus.COMPLETED);
        }

        return tournamentMatchRepository.save(match);
    }


    @Transactional
    public TournamentMatch setTechnicalResult(UUID matchId, UUID teamId, TechResult techResult) {
        TournamentMatch match = tournamentMatchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));

        Team team = match.getTeamScores().stream()
                .map(TournamentTeamScore::getTeam)
                .filter(t -> t.getId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Team not found in match: " + teamId));

        match.setTechResult(team, techResult);

        if (techResult != TechResult.NONE) {
            match.setStatus(TournamentMatch.MatchStatus.COMPLETED);
        }

        return tournamentMatchRepository.save(match);
    }
    @Transactional
    public List<TournamentMatch> generateBracket(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        if (tournament.getTeams().size() < 2) {
            throw new IllegalStateException("Tournament needs at least 2 teams to generate a bracket");
        }

        List<TournamentMatch> existingMatches = tournamentMatchRepository.findByTournament(tournament);
        tournamentMatchRepository.deleteAll(existingMatches);

        List<Team> teams = new ArrayList<>(tournament.getTeams());
        Collections.shuffle(teams);

        int numTeams = teams.size();
        int numRounds = (int) Math.ceil(Math.log(numTeams) / Math.log(2));
        int totalMatches = (int) Math.pow(2, numRounds) - 1;

        List<TournamentMatch> matches = new ArrayList<>();

        int firstRoundMatches = numTeams / 2;
        for (int i = 0; i < firstRoundMatches; i++) {
            TournamentMatch match = new TournamentMatch();
            match.setTournament(tournament);
            match.setRound(1);
            match.setMatchNumber("R1-M" + (i + 1));

            match.addTeam(teams.get(i * 2));
            match.addTeam(teams.get(i * 2 + 1));

            matches.add(match);
        }

        if (numTeams % 2 != 0 && numTeams > 2) {
            TournamentMatch match = new TournamentMatch();
            match.setTournament(tournament);
            match.setRound(1);
            match.setMatchNumber("R1-M" + (firstRoundMatches + 1));

            match.addTeam(teams.get(numTeams - 1));

            matches.add(match);
        }

        int matchesInRound = firstRoundMatches;
        for (int round = 2; round <= numRounds; round++) {
            matchesInRound = (matchesInRound + 1) / 2;
            for (int i = 0; i < matchesInRound; i++) {
                TournamentMatch match = new TournamentMatch();
                match.setTournament(tournament);
                match.setRound(round);
                match.setMatchNumber("R" + round + "-M" + (i + 1));
                matches.add(match);
            }
        }

        return tournamentMatchRepository.saveAll(matches);
    }

    @Transactional
    public void updateBracket(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        List<TournamentMatch> matches = tournamentMatchRepository.findByTournamentOrderByRoundAscMatchNumberAsc(tournament);

        Map<Integer, List<TournamentMatch>> matchesByRound = matches.stream()
                .collect(Collectors.groupingBy(TournamentMatch::getRound));

        int maxRound = matchesByRound.keySet().stream().max(Integer::compareTo).orElse(0);

        for (int round = 1; round < maxRound; round++) {
            List<TournamentMatch> roundMatches = matchesByRound.get(round);
            List<TournamentMatch> nextRoundMatches = matchesByRound.get(round + 1);

            if (roundMatches != null && nextRoundMatches != null) {
                updateNextRoundMatches(roundMatches, nextRoundMatches);
            }
        }

        tournamentMatchRepository.saveAll(matches);
    }

    private void updateNextRoundMatches(List<TournamentMatch> currentRoundMatches, List<TournamentMatch> nextRoundMatches) {
        int nextRoundMatchIndex = 0;

        for (int i = 0; i < currentRoundMatches.size(); i += 2) {
            if (nextRoundMatchIndex >= nextRoundMatches.size()) {
                break;
            }

            TournamentMatch nextRoundMatch = nextRoundMatches.get(nextRoundMatchIndex++);

            nextRoundMatch.getTeamScores().clear();

            TournamentMatch match1 = currentRoundMatches.get(i);
            Team winner1 = match1.getWinner();

            if (i + 1 >= currentRoundMatches.size()) {
                if (winner1 != null) {
                    nextRoundMatch.addTeam(winner1);
                }
                continue;
            }

            TournamentMatch match2 = currentRoundMatches.get(i + 1);
            Team winner2 = match2.getWinner();

            if (winner1 != null) {
                nextRoundMatch.addTeam(winner1);
            }

            if (winner2 != null) {
                nextRoundMatch.addTeam(winner2);
            }
        }
    }
}
