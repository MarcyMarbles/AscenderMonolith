package kz.saya.project.ascender.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import kz.saya.project.ascender.Entities.JoinRequest;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Services.JoinRequestService;
import kz.saya.project.ascender.Services.TeamService;
import kz.saya.sbasesecurity.Security.JwtUtils;
import kz.saya.sbasesecurity.Service.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/join-requests")
public class JoinRequestController extends BaseController {

    private final JoinRequestService joinRequestService;
    private final TeamService teamService;
    private final JwtUtils jwtUtils;

    @Autowired
    public JoinRequestController(JoinRequestService joinRequestService, TeamService teamService, JwtUtils jwtUtils, UserSecurityService userSecurityService) {
        super(userSecurityService);
        this.joinRequestService = joinRequestService;
        this.teamService = teamService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<?> getAllJoinRequests(HttpServletRequest request) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only administrators can view all join requests");
        }
        return ResponseEntity.ok(joinRequestService.getAllJoinRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJoinRequestById(@PathVariable UUID id, HttpServletRequest request) {
        Optional<JoinRequest> joinRequestOpt = joinRequestService.getJoinRequestById(id);

        if (joinRequestOpt.isEmpty()) {
            return notFound("Join request not found");
        }

        JoinRequest joinRequest = joinRequestOpt.get();

        boolean isAdmin = hasAdminRole(request);
        boolean isCreator = isTeamCreator(request, joinRequest.getTeam().getCreator());

        if (!isAdmin && !isCreator) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to view this join request");
        }

        return ResponseEntity.ok(joinRequest);
    }

    @PostMapping
    public ResponseEntity<?> createJoinRequest(
            @RequestParam UUID teamId,
            @RequestParam UUID tournamentId,
            @RequestParam(required = false) String message,
            HttpServletRequest request) {
        Optional<Team> teamOpt = teamService.getTeamById(teamId);
        if (teamOpt.isEmpty()) {
            return badRequest("Team not found");
        }

        Team team = teamOpt.get();
        PlayerProfile creator = team.getCreator();

        if (creator == null || !isTeamCreator(request, creator)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only the team creator can request to join a tournament");
        }

        try {
            JoinRequest joinRequest = joinRequestService.createJoinRequest(teamId, tournamentId, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(joinRequest);
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateJoinRequestStatus(
            @PathVariable UUID id,
            @RequestParam JoinRequest.RequestStatus status,
            @RequestParam(required = false) String responseMessage,
            HttpServletRequest request) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only administrators can update join request status");
        }

        try {
            JoinRequest joinRequest = joinRequestService.updateJoinRequestStatus(id, status, responseMessage);
            return ResponseEntity.ok(joinRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean isTeamCreator(HttpServletRequest request, PlayerProfile creator) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);
        String login = jwtUtils.extractLogin(token);
        if (login == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return creator != null && creator.getEmail() != null && creator.getEmail().equals(login);
    }

    private boolean hasAdminRole(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);
        String login = jwtUtils.extractLogin(token);
        if (login == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<?> getJoinRequestsByTournament(@PathVariable UUID tournamentId, HttpServletRequest request) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only administrators can view all join requests for a tournament");
        }

        try {
            List<JoinRequest> joinRequests = joinRequestService.getJoinRequestsByTournament(tournamentId);
            return ResponseEntity.ok(joinRequests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tournament/{tournamentId}/pending")
    public ResponseEntity<?> getPendingJoinRequestsByTournament(@PathVariable UUID tournamentId, HttpServletRequest request) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only administrators can view pending join requests for a tournament");
        }

        try {
            List<JoinRequest> joinRequests = joinRequestService.getPendingJoinRequestsByTournament(tournamentId);
            return ResponseEntity.ok(joinRequests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getJoinRequestsByTeam(@PathVariable UUID teamId, HttpServletRequest request) {
        boolean isAdmin = hasAdminRole(request);
        if (!isAdmin) {
            Optional<Team> teamOpt = teamService.getTeamById(teamId);
            if (teamOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Team not found");
            }

            Team team = teamOpt.get();
            PlayerProfile creator = team.getCreator();

            if (creator == null || !isTeamCreator(request, creator)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only the team creator or administrators can view team join requests");
            }
        }

        try {
            List<JoinRequest> joinRequests = joinRequestService.getJoinRequestsByTeam(teamId);
            return ResponseEntity.ok(joinRequests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJoinRequest(@PathVariable UUID id, HttpServletRequest request) {
        Optional<JoinRequest> joinRequestOpt = joinRequestService.getJoinRequestById(id);
        if (joinRequestOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        JoinRequest joinRequest = joinRequestOpt.get();

        boolean isAdmin = hasAdminRole(request);
        if (!isAdmin) {
            Team team = joinRequest.getTeam();
            PlayerProfile creator = team.getCreator();

            if (creator == null || !isTeamCreator(request, creator)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only the team creator or administrators can delete join requests");
            }
        }

        try {
            joinRequestService.deleteJoinRequest(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
