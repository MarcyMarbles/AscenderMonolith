package kz.saya.project.ascender.Services;

import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Repositories.PlayerProfileRepository;
import kz.saya.project.ascender.Repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerProfileRepository playerProfileRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, PlayerProfileRepository playerProfileRepository) {
        this.teamRepository = teamRepository;
        this.playerProfileRepository = playerProfileRepository;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(UUID id) {
        return teamRepository.findById(id);
    }

    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    public void deleteTeam(UUID id) {
        teamRepository.deleteById(id);
    }

    public Optional<Team> addPlayerToTeam(UUID teamId, UUID playerId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        Optional<PlayerProfile> playerOpt = playerProfileRepository.findById(playerId);

        if (teamOpt.isPresent() && playerOpt.isPresent()) {
            Team team = teamOpt.get();
            PlayerProfile player = playerOpt.get();

            List<PlayerProfile> players = team.getPlayers();
            if (!players.contains(player)) {
                players.add(player);
                team.setPlayers(players);
                return Optional.of(teamRepository.save(team));
            }
            return Optional.of(team);
        }

        return Optional.empty();
    }

    public Optional<Team> removePlayerFromTeam(UUID teamId, UUID playerId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);

        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();

            List<PlayerProfile> players = team.getPlayers();
            players.removeIf(player -> player.getId().equals(playerId));
            team.setPlayers(players);

            return Optional.of(teamRepository.save(team));
        }

        return Optional.empty();
    }

    public List<Team> findTeamsByGame(UUID gameId) {
        return teamRepository.findAll().stream()
                .filter(team -> team.getGames().stream()
                        .anyMatch(game -> game.getId().equals(gameId)))
                .collect(Collectors.toList());
    }

    public List<PlayerProfile> findTeammatesByGame(UUID gameId) {
        List<Team> teamsForGame = findTeamsByGame(gameId);

        return teamsForGame.stream()
                .flatMap(team -> team.getPlayers().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
