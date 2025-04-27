package kz.saya.project.ascender.Services;

import kz.saya.project.ascender.DTO.TeamDTO;
import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Repositories.GamesRepository;
import kz.saya.project.ascender.Repositories.PlayerProfileRepository;
import kz.saya.project.ascender.Repositories.TeamRepository;
import kz.saya.sbase.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerProfileRepository playerProfileRepository;
    private final GamesRepository gamesRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, PlayerProfileRepository playerProfileRepository, GamesRepository gamesRepository) {
        this.teamRepository = teamRepository;
        this.playerProfileRepository = playerProfileRepository;
        this.gamesRepository = gamesRepository;
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

    /**
     * Converts a TeamDTO to a Team entity
     * @param teamDTO the DTO to convert
     * @return the converted Team entity
     */
    public Team convertToEntity(TeamDTO teamDTO) {
        Team team = new Team();

        // Copy basic fields
        team.setId(teamDTO.getId());
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        team.setLogo(teamDTO.getLogo());
        team.setBackground(teamDTO.getBackground());
        team.setWebsite(teamDTO.getWebsite());
        team.setDiscord(teamDTO.getDiscord());
        team.setVk(teamDTO.getVk());
        team.setInstagram(teamDTO.getInstagram());
        team.setTiktok(teamDTO.getTiktok());

        // Convert game IDs to Game entities
        if (teamDTO.getGameIds() != null && !teamDTO.getGameIds().isEmpty()) {
            Set<Games> games = new HashSet<>();
            for (UUID gameId : teamDTO.getGameIds()) {
                gamesRepository.findById(gameId).ifPresent(games::add);
            }
            team.setGames(games);
        }

        return team;
    }

    /**
     * Converts a Team entity to a TeamDTO
     * @param team the entity to convert
     * @return the converted TeamDTO
     */
    public TeamDTO convertToDto(Team team) {
        TeamDTO teamDTO = new TeamDTO();

        // Copy basic fields
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setDescription(team.getDescription());
        teamDTO.setLogo(team.getLogo());
        teamDTO.setBackground(team.getBackground());
        teamDTO.setWebsite(team.getWebsite());
        teamDTO.setDiscord(team.getDiscord());
        teamDTO.setVk(team.getVk());
        teamDTO.setInstagram(team.getInstagram());
        teamDTO.setTiktok(team.getTiktok());

        // Convert Game entities to game IDs
        if (team.getGames() != null && !team.getGames().isEmpty()) {
            Set<UUID> gameIds = team.getGames().stream()
                    .map(Games::getId)
                    .collect(Collectors.toSet());
            teamDTO.setGameIds(gameIds);
        }

        return teamDTO;
    }

    /**
     * Saves a team from a DTO
     * @param teamDTO the DTO to save
     * @return the saved team as a DTO
     */
    public TeamDTO saveTeamFromDto(TeamDTO teamDTO) {
        Team team = convertToEntity(teamDTO);
        Team savedTeam = teamRepository.save(team);
        return convertToDto(savedTeam);
    }

    /**
     * Updates a team from a DTO
     * @param id the ID of the team to update
     * @param teamDTO the DTO with updated data
     * @return the updated team as a DTO, or empty if the team doesn't exist
     */
    public Optional<TeamDTO> updateTeamFromDto(UUID id, TeamDTO teamDTO) {
        if (!teamRepository.existsById(id)) {
            return Optional.empty();
        }

        teamDTO.setId(id);
        Team team = convertToEntity(teamDTO);
        Team savedTeam = teamRepository.save(team);
        return Optional.of(convertToDto(savedTeam));
    }

    /**
     * Checks if a user is the creator of a team
     * @param team the team to check
     * @param user the user to check
     * @return true if the user is the creator of the team, false otherwise
     */
    public boolean isTeamCreator(Team team, User user) {
        if (team == null || user == null || team.getCreator() == null) {
            return false;
        }

        return team.getCreator().getUser().getId().equals(user.getId());
    }

    /**
     * Checks if a player is a member of a team
     * @param team the team to check
     * @param playerProfile the player profile to check
     * @return true if the player is a member of the team, false otherwise
     */
    public boolean isTeamMember(Team team, PlayerProfile playerProfile) {
        if (team == null || playerProfile == null) {
            return false;
        }

        return team.getPlayers().contains(playerProfile);
    }

    /**
     * Initiates a votekick for a player in a team
     * @param teamId the ID of the team
     * @param initiatorId the ID of the player initiating the votekick
     * @param targetId the ID of the player to be kicked
     * @return the updated team if successful, empty otherwise
     */
    public Optional<Team> initiateVotekick(UUID teamId, UUID initiatorId, UUID targetId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        Optional<PlayerProfile> initiatorOpt = playerProfileRepository.findById(initiatorId);
        Optional<PlayerProfile> targetOpt = playerProfileRepository.findById(targetId);

        if (teamOpt.isEmpty() || initiatorOpt.isEmpty() || targetOpt.isEmpty()) {
            return Optional.empty();
        }

        Team team = teamOpt.get();
        PlayerProfile initiator = initiatorOpt.get();
        PlayerProfile target = targetOpt.get();

        // Check if initiator is a team member
        if (!isTeamMember(team, initiator)) {
            return Optional.empty();
        }

        // Check if target is a team member
        if (!isTeamMember(team, target)) {
            return Optional.empty();
        }

        return removePlayerFromTeam(teamId, targetId);
    }
}
