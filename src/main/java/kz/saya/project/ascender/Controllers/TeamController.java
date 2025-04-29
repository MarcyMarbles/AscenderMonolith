package kz.saya.project.ascender.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import kz.saya.project.ascender.DTO.PlayerProfileDTO;
import kz.saya.project.ascender.DTO.TeamDTO;
import kz.saya.project.ascender.DTO.TeamPlayerDTO;
import kz.saya.project.ascender.DTO.VotekickDTO;
import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Services.PlayerProfileService;
import kz.saya.project.ascender.Services.TeamService;
import kz.saya.sbasecore.Entity.User;
import kz.saya.sbasecore.Service.UserService;
import kz.saya.sbasesecurity.Service.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController extends BaseController {

    private final TeamService teamService;
    private final PlayerProfileService playerProfileService;
    private final UserService userService;

    @Autowired
    public TeamController(TeamService teamService, PlayerProfileService playerProfileService,
                          UserService userService, UserSecurityService userSecurityService) {
        super(userSecurityService);
        this.teamService = teamService;
        this.playerProfileService = playerProfileService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<TeamDTO> teamDTOs = teamService.getAllTeams().stream()
                .map(teamService::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teamDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable UUID id) {
        Optional<Team> team = teamService.getTeamById(id);
        return team.map(t -> ResponseEntity.ok(teamService.convertToDto(t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return unauthorized("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (playerProfileOpt.isEmpty()) {
            return badRequest("You need to create a player profile first");
        }

        PlayerProfile playerProfile = playerProfileOpt.get();

        // Check if player is already in a team
        List<Team> allTeams = teamService.getAllTeams();
        boolean isInTeam = allTeams.stream()
                .anyMatch(team -> team.getPlayers().contains(playerProfile));

        if (isInTeam) {
            return badRequest("You are already in a team");
        }

        // Set the creator of the team
        Team team = teamService.convertToEntity(teamDTO);
        team.setCreator(playerProfile);

        // Add the creator as a player in the team
        if (team.getPlayers() == null) {
            team.setPlayers(new java.util.ArrayList<>());
        }
        team.getPlayers().add(playerProfile);

        // Save the team
        Team savedTeam = teamService.saveTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.convertToDto(savedTeam));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable UUID id, @RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return unauthorized("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (playerProfileOpt.isEmpty()) {
            return badRequest("You need to create a player profile first");
        }

        // Check if team exists
        Optional<Team> teamOpt = teamService.getTeamById(id);
        if (teamOpt.isEmpty()) {
            return notFound("Team not found");
        }

        Team team = teamOpt.get();

        // Check if user is the team creator
        if (!teamService.isTeamCreator(team, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to edit this team");
        }

        // Update the team
        teamDTO.setId(id);
        Optional<TeamDTO> updatedTeamDTO = teamService.updateTeamFromDto(id, teamDTO);
        return updatedTeamDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable UUID id, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return unauthorized("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (playerProfileOpt.isEmpty()) {
            return badRequest("You need to create a player profile first");
        }

        // Check if team exists
        Optional<Team> teamOpt = teamService.getTeamById(id);
        if (teamOpt.isEmpty()) {
            return notFound("Team not found");
        }

        Team team = teamOpt.get();

        // Check if user is the team creator
        if (!teamService.isTeamCreator(team, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to delete this team");
        }

        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/players")
    public ResponseEntity<?> addPlayerToTeam(@PathVariable UUID id, @RequestBody TeamPlayerDTO playerDTO, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return unauthorized("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (playerProfileOpt.isEmpty()) {
            return badRequest("You need to create a player profile first");
        }

        // Check if team exists
        Optional<Team> teamOpt = teamService.getTeamById(id);
        if (teamOpt.isEmpty()) {
            return notFound("Team not found");
        }

        Team team = teamOpt.get();

        // Check if user is the team creator
        if (!teamService.isTeamCreator(team, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to add players to this team");
        }

        Optional<Team> updatedTeam = teamService.addPlayerToTeam(id, playerDTO.getPlayerId());
        return updatedTeam.map(t -> ResponseEntity.ok(teamService.convertToDto(t)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<?> removePlayerFromTeam(@PathVariable UUID teamId, @PathVariable UUID playerId, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return unauthorized("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (playerProfileOpt.isEmpty()) {
            return badRequest("You need to create a player profile first");
        }

        // Check if team exists
        Optional<Team> teamOpt = teamService.getTeamById(teamId);
        if (teamOpt.isEmpty()) {
            return notFound("Team not found");
        }

        Team team = teamOpt.get();

        // Check if user is the team creator
        if (!teamService.isTeamCreator(team, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to remove players from this team");
        }

        Optional<Team> updatedTeam = teamService.removePlayerFromTeam(teamId, playerId);
        return updatedTeam.map(t -> ResponseEntity.ok(teamService.convertToDto(t)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<TeamDTO>> findTeamsByGame(@PathVariable UUID gameId) {
        List<TeamDTO> teamDTOs = teamService.findTeamsByGame(gameId).stream()
                .map(teamService::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teamDTOs);
    }

    @GetMapping("/game/{gameId}/players")
    public ResponseEntity<List<PlayerProfileDTO>> findTeammatesByGame(@PathVariable UUID gameId) {
        List<PlayerProfileDTO> playerDTOs = teamService.findTeammatesByGame(gameId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(playerDTOs);
    }

    @PostMapping("/{teamId}/votekick")
    public ResponseEntity<?> initiateVotekick(
            @PathVariable UUID teamId,
            @RequestBody VotekickDTO votekickDTO,
            HttpServletRequest request) {

        User user = extractUserFromToken(request);
        if (user == null) {
            return unauthorized("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> initiatorProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (initiatorProfileOpt.isEmpty()) {
            return badRequest("You need to create a player profile first");
        }

        PlayerProfile initiatorProfile = initiatorProfileOpt.get();

        // Check if team exists
        Optional<Team> teamOpt = teamService.getTeamById(teamId);
        if (teamOpt.isEmpty()) {
            return notFound("Team not found");
        }

        Team team = teamOpt.get();

        // Check if initiator is a team member
        if (!teamService.isTeamMember(team, initiatorProfile)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not a member of this team");
        }

        // Initiate votekick
        Optional<Team> updatedTeamOpt = teamService.initiateVotekick(teamId, initiatorProfile.getId(), votekickDTO.getTargetId());

        if (updatedTeamOpt.isEmpty()) {
            return badRequest("Failed to initiate votekick");
        }

        return ResponseEntity.ok(teamService.convertToDto(updatedTeamOpt.get()));
    }

    // Conversion method for PlayerProfile to PlayerProfileDTO
    private PlayerProfileDTO convertToDto(PlayerProfile playerProfile) {
        PlayerProfileDTO dto = new PlayerProfileDTO();
        dto.setId(playerProfile.getId());
        dto.setNickname(playerProfile.getCallingName());
        dto.setSkillLevel(playerProfile.getSkillLevel());
        dto.setBio(playerProfile.getBio());
        dto.setLookingForTeam(playerProfile.isLookingForTeam());

        if (playerProfile.getUser() != null) {
            dto.setUserId(playerProfile.getUser().getId());
        }

        if (playerProfile.getPreferredGames() != null) {
            dto.setGameIds(playerProfile.getPreferredGames().stream()
                    .map(Games::getId)
                    .collect(Collectors.toList()));
        }

        if (playerProfile.getAvatar() != null) {
            dto.setAvatarId(playerProfile.getAvatar().getId());
        }

        return dto;
    }
}
