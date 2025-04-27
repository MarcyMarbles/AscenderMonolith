package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Get all teams
     * @return List of all teams
     */
    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    /**
     * Get team by ID
     * @param id Team ID
     * @return Team if found, 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable UUID id) {
        Optional<Team> team = teamService.getTeamById(id);
        return team.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new team
     * @param team Team to create
     * @return Created team
     */
    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.saveTeam(team));
    }

    /**
     * Update an existing team
     * @param id Team ID
     * @param team Updated team data
     * @return Updated team if found, 404 otherwise
     */
    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable UUID id, @RequestBody Team team) {
        if (!teamService.getTeamById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        team.setId(id);
        return ResponseEntity.ok(teamService.saveTeam(team));
    }

    /**
     * Delete a team
     * @param id Team ID
     * @return 204 No Content if successful, 404 if team not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        if (!teamService.getTeamById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a player to a team
     * @param id Team ID
     * @param requestData Map containing playerId
     * @return Updated team if successful, 400 Bad Request if invalid data
     */
    @PostMapping("/{id}/players")
    public ResponseEntity<Team> addPlayerToTeam(@PathVariable UUID id, @RequestBody Map<String, Object> requestData) {
        UUID playerId = UUID.fromString((String) requestData.get("playerId"));
        
        Optional<Team> team = teamService.addPlayerToTeam(id, playerId);
        return team.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Remove a player from a team
     * @param teamId Team ID
     * @param playerId Player ID
     * @return Updated team if successful, 400 Bad Request if invalid data
     */
    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Team> removePlayerFromTeam(@PathVariable UUID teamId, @PathVariable UUID playerId) {
        Optional<Team> team = teamService.removePlayerFromTeam(teamId, playerId);
        return team.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Find teams by game
     * @param gameId Game ID
     * @return List of teams that play the specified game
     */
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Team>> findTeamsByGame(@PathVariable UUID gameId) {
        return ResponseEntity.ok(teamService.findTeamsByGame(gameId));
    }

    /**
     * Find available teammates for a specific game
     * @param gameId Game ID
     * @return List of players who play the specified game
     */
    @GetMapping("/game/{gameId}/players")
    public ResponseEntity<List<PlayerProfile>> findTeammatesByGame(@PathVariable UUID gameId) {
        return ResponseEntity.ok(teamService.findTeammatesByGame(gameId));
    }
}