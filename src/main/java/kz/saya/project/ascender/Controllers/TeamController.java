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

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable UUID id) {
        Optional<Team> team = teamService.getTeamById(id);
        return team.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.saveTeam(team));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable UUID id, @RequestBody Team team) {
        if (!teamService.getTeamById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        team.setId(id);
        return ResponseEntity.ok(teamService.saveTeam(team));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        if (!teamService.getTeamById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/players")
    public ResponseEntity<Team> addPlayerToTeam(@PathVariable UUID id, @RequestBody Map<String, Object> requestData) {
        UUID playerId = UUID.fromString((String) requestData.get("playerId"));

        Optional<Team> team = teamService.addPlayerToTeam(id, playerId);
        return team.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Team> removePlayerFromTeam(@PathVariable UUID teamId, @PathVariable UUID playerId) {
        Optional<Team> team = teamService.removePlayerFromTeam(teamId, playerId);
        return team.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Team>> findTeamsByGame(@PathVariable UUID gameId) {
        return ResponseEntity.ok(teamService.findTeamsByGame(gameId));
    }

    @GetMapping("/game/{gameId}/players")
    public ResponseEntity<List<PlayerProfile>> findTeammatesByGame(@PathVariable UUID gameId) {
        return ResponseEntity.ok(teamService.findTeammatesByGame(gameId));
    }
}
