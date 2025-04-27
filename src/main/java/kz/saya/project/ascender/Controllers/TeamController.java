package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.DTO.TeamDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
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
    public ResponseEntity<TeamDTO> createTeam(@RequestBody TeamDTO teamDTO) {
        TeamDTO savedTeamDTO = teamService.saveTeamFromDto(teamDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTeamDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable UUID id, @RequestBody TeamDTO teamDTO) {
        Optional<TeamDTO> updatedTeamDTO = teamService.updateTeamFromDto(id, teamDTO);
        return updatedTeamDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
    public ResponseEntity<TeamDTO> addPlayerToTeam(@PathVariable UUID id, @RequestBody Map<String, Object> requestData) {
        UUID playerId = UUID.fromString((String) requestData.get("playerId"));

        Optional<Team> team = teamService.addPlayerToTeam(id, playerId);
        return team.map(t -> ResponseEntity.ok(teamService.convertToDto(t)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<TeamDTO> removePlayerFromTeam(@PathVariable UUID teamId, @PathVariable UUID playerId) {
        Optional<Team> team = teamService.removePlayerFromTeam(teamId, playerId);
        return team.map(t -> ResponseEntity.ok(teamService.convertToDto(t)))
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
}
