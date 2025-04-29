package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.Entities.JoinRequest;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Services.JoinRequestService;
import kz.saya.project.ascender.Services.TeamService;
import kz.saya.sbasecore.Entity.User;
import kz.saya.sbasesecurity.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/join-requests")
public class JoinRequestController extends BaseController {

    private final AuthService authService;
    private final JoinRequestService joinRequestService;
    private final TeamService teamService;

    public JoinRequestController(AuthService authService, AuthService authService1, JoinRequestService joinRequestService, TeamService teamService) {
        super(authService);
        this.authService = authService1;
        this.joinRequestService = joinRequestService;
        this.teamService = teamService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JoinRequest>> getAll() {
        List<JoinRequest> list = joinRequestService.getAllJoinRequests();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JoinRequest> getById(@PathVariable UUID id) {
        JoinRequest jr = joinRequestService.getJoinRequestById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Join request not found"));

        User me = authService.getAuthenticatedUser();
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        boolean isAdmin = hasRole("ADMIN");
        boolean isCreator = jr.getTeam().getCreator().getUser().getId().equals(me.getId());
        if (!isAdmin && !isCreator) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission");
        }

        return ResponseEntity.ok(jr);
    }

    // 3) Создание — только создатель команды
    @PostMapping
    public ResponseEntity<JoinRequest> create(
            @RequestParam UUID teamId,
            @RequestParam UUID tournamentId,
            @RequestParam(required = false) String message
    ) {
        User me = authService.getAuthenticatedUser();
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        Team team = teamService.getTeamById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team not found"));

        if (!team.getCreator().getUser().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only team creator can request");
        }

        JoinRequest jr;
        try {
            jr = joinRequestService.createJoinRequest(teamId, tournamentId, message);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(jr);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JoinRequest> updateStatus(
            @PathVariable UUID id,
            @RequestParam JoinRequest.RequestStatus status,
            @RequestParam(required = false) String responseMessage
    ) {
        JoinRequest jr;
        try {
            jr = joinRequestService.updateJoinRequestStatus(id, status, responseMessage);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok(jr);
    }

    // 5) Заявки по турниру — только админы
    @GetMapping("/tournament/{tournamentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JoinRequest>> byTournament(@PathVariable UUID tournamentId) {
        List<JoinRequest> list;
        try {
            list = joinRequestService.getJoinRequestsByTournament(tournamentId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok(list);
    }

    // 6) Только админы — pending по турниру
    @GetMapping("/tournament/{tournamentId}/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JoinRequest>> pendingByTournament(@PathVariable UUID tournamentId) {
        List<JoinRequest> list;
        try {
            list = joinRequestService.getPendingJoinRequestsByTournament(tournamentId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok(list);
    }

    // 7) Заявки по команде — admin или создатель
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<JoinRequest>> byTeam(@PathVariable UUID teamId) {
        User me = authService.getAuthenticatedUser();
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        boolean isAdmin = hasRole("ADMIN");
        if (!isAdmin) {
            Team team = teamService.getTeamById(teamId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team not found"));
            if (!team.getCreator().getUser().getId().equals(me.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission");
            }
        }

        List<JoinRequest> list;
        try {
            list = joinRequestService.getJoinRequestsByTeam(teamId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok(list);
    }

    // 8) Удаление — admin или создатель команды
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        JoinRequest jr = joinRequestService.getJoinRequestById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Join request not found"));

        User me = authService.getAuthenticatedUser();
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        boolean isAdmin = hasRole("ADMIN");
        if (!isAdmin && !jr.getTeam().getCreator().getUser().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission");
        }

        joinRequestService.deleteJoinRequest(id);
        return ResponseEntity.noContent().build();
    }

    // Утилита: проверка роли у текущего Authentication
    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
