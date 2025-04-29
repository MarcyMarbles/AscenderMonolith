package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.DTO.*;
import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Services.PlayerProfileService;
import kz.saya.project.ascender.Services.TeamService;
import kz.saya.sbasecore.Entity.User;
import kz.saya.sbasesecurity.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
@PreAuthorize("isAuthenticated()")  // любой залогиненный пользователь
public class TeamController extends BaseController {

    private final TeamService teamService;
    private final PlayerProfileService playerProfileService;

    public TeamController(AuthService authService,
                          TeamService teamService,
                          PlayerProfileService playerProfileService) {
        super(authService);
        this.teamService = teamService;
        this.playerProfileService = playerProfileService;
    }

    // 1) Список всех команд — доступен всем
    @GetMapping
    public List<TeamDTO> list() {
        return teamService.getAllTeams().stream()
                .map(teamService::convertToDto)
                .collect(Collectors.toList());
    }

    // 2) Детали одной команды — доступен всем
    @GetMapping("/{id}")
    public TeamDTO getById(@PathVariable UUID id) {
        Team team = teamService.getTeamById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Team not found"));
        return teamService.convertToDto(team);
    }

    // 3) Создать команду — любой залогиненный
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamDTO create(@RequestBody TeamDTO dto) {
        User me = currentUser();
        if (me == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        PlayerProfile profile = playerProfileService
                .findPlayerProfileByUser(me)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "You need to create a player profile first"));

        // проверяем, что ещё не в команде
        boolean alreadyInTeam = teamService.getAllTeams().stream()
                .anyMatch(t -> t.getPlayers().contains(profile));
        if (alreadyInTeam) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You are already in a team");
        }

        Team team = teamService.convertToEntity(dto);
        team.setCreator(profile);
        team.getPlayers().add(profile);
        Team saved = teamService.saveTeam(team);
        return teamService.convertToDto(saved);
    }

    // 4) Обновить команду — только админ или создатель команды
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @teamSecurity.isTeamCreator(principal.username, #id)")
    public ResponseEntity<TeamDTO> update(@PathVariable UUID id,
                                          @RequestBody TeamDTO dto) {
        // проверка на существование
        teamService.getTeamById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Team not found"));

        dto.setId(id);
        return teamService.updateTeamFromDto(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @teamSecurity.isTeamCreator(principal.username, #id)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        if (teamService.getTeamById(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Team not found");
        }
        teamService.deleteTeam(id);
    }

    @PostMapping("/{id}/players")
    @PreAuthorize("hasRole('ADMIN') or @teamSecurity.isTeamCreator(principal.username, #id)")
    public TeamDTO addPlayer(@PathVariable UUID id,
                             @RequestBody TeamPlayerDTO dto) {
        return teamService.addPlayerToTeam(id, dto.getPlayerId())
                .map(teamService::convertToDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Failed to add player"));
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    @PreAuthorize("hasRole('ADMIN') or @teamSecurity.isTeamCreator(principal.username, #teamId)")
    public TeamDTO removePlayer(@PathVariable UUID teamId,
                                @PathVariable UUID playerId) {
        return teamService.removePlayerFromTeam(teamId, playerId)
                .map(teamService::convertToDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Failed to remove player"));
    }

    @GetMapping("/game/{gameId}")
    public List<TeamDTO> findByGame(@PathVariable UUID gameId) {
        return teamService.findTeamsByGame(gameId).stream()
                .map(teamService::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/game/{gameId}/players")
    public List<PlayerProfileDTO> findPlayersByGame(@PathVariable UUID gameId) {
        return teamService.findTeammatesByGame(gameId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PlayerProfileDTO convertToDto(PlayerProfile p) {
        PlayerProfileDTO dto = new PlayerProfileDTO();
        dto.setId(p.getId());
        dto.setNickname(p.getCallingName());
        dto.setSkillLevel(p.getSkillLevel());
        dto.setBio(p.getBio());
        dto.setLookingForTeam(p.isLookingForTeam());
        if (p.getPreferredGames() != null) {
            dto.setGameIds(p.getPreferredGames().stream()
                    .map(Games::getId).toList());
        }
        return dto;
    }
}
