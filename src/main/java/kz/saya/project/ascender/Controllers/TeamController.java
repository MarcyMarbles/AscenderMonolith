package kz.saya.project.ascender.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import kz.saya.project.ascender.DTO.TeamDTO;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final PlayerProfileService playerProfileService;
    private final UserService userService;
    private final UserSecurityService userSecurityService;

    @Autowired
    public TeamController(TeamService teamService, PlayerProfileService playerProfileService, UserService userService, UserSecurityService userSecurityService) {
        this.teamService = teamService;
        this.playerProfileService = playerProfileService;
        this.userService = userService;
        this.userSecurityService = userSecurityService;
    }

    private User extractUserFromToken(HttpServletRequest request) {
        return userSecurityService.extractUserFromToken(request.getHeader("Authorization"));
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (playerProfileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You need to create a player profile first");
        }

        PlayerProfile playerProfile = playerProfileOpt.get();

        // Check if player is already in a team
        List<Team> allTeams = teamService.getAllTeams();
        boolean isInTeam = allTeams.stream()
                .anyMatch(team -> team.getPlayers().contains(playerProfile));

        if (isInTeam) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are already in a team");
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (playerProfileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You need to create a player profile first");
        }

        // Check if team exists
        Optional<Team> teamOpt = teamService.getTeamById(id);
        if (teamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (playerProfileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You need to create a player profile first");
        }

        // Check if team exists
        Optional<Team> teamOpt = teamService.getTeamById(id);
        if (teamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
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
    public ResponseEntity<?> addPlayerToTeam(@PathVariable UUID id, @RequestBody Map<String, Object> requestData, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (playerProfileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You need to create a player profile first");
        }

        // Check if team exists
        Optional<Team> teamOpt = teamService.getTeamById(id);
        if (teamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Team team = teamOpt.get();

        // Check if user is the team creator
        if (!teamService.isTeamCreator(team, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to add players to this team");
        }

        UUID playerId = UUID.fromString((String) requestData.get("playerId"));

        Optional<Team> updatedTeam = teamService.addPlayerToTeam(id, playerId);
        return updatedTeam.map(t -> ResponseEntity.ok(teamService.convertToDto(t)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<?> removePlayerFromTeam(@PathVariable UUID teamId, @PathVariable UUID playerId, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (playerProfileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You need to create a player profile first");
        }

        // Check if team exists
        Optional<Team> teamOpt = teamService.getTeamById(teamId);
        if (teamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
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
    public ResponseEntity<List<PlayerProfile>> findTeammatesByGame(@PathVariable UUID gameId) {
        return ResponseEntity.ok(teamService.findTeammatesByGame(gameId));
    }

    @PostMapping("/{teamId}/votekick/{targetId}")
    public ResponseEntity<?> initiateVotekick(
            @PathVariable UUID teamId,
            @PathVariable UUID targetId,
            HttpServletRequest request) {

        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Check if user has a PlayerProfile
        Optional<PlayerProfile> initiatorProfileOpt = playerProfileService.findPlayerProfileByUser(user);
        if (initiatorProfileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You need to create a player profile first");
        }

        PlayerProfile initiatorProfile = initiatorProfileOpt.get();

        // Check if team exists
        Optional<Team> teamOpt = teamService.getTeamById(teamId);
        if (teamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Team team = teamOpt.get();

        // Check if initiator is a team member
        if (!teamService.isTeamMember(team, initiatorProfile)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not a member of this team");
        }

        // Initiate votekick
        Optional<Team> updatedTeamOpt = teamService.initiateVotekick(teamId, initiatorProfile.getId(), targetId);

        if (updatedTeamOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to initiate votekick");
        }

        return ResponseEntity.ok(teamService.convertToDto(updatedTeamOpt.get()));
    }
}
