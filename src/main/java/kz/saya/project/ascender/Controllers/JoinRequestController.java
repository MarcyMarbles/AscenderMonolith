package kz.saya.project.ascender.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import kz.saya.project.ascender.Entities.JoinRequest;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Services.JoinRequestService;
import kz.saya.project.ascender.Services.TeamService;
import kz.saya.sbase.Security.JwtUtils;
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
public class JoinRequestController {

    private final JoinRequestService joinRequestService;
    private final TeamService teamService;
    private final JwtUtils jwtUtils;

    @Autowired
    public JoinRequestController(JoinRequestService joinRequestService, TeamService teamService, JwtUtils jwtUtils) {
        this.joinRequestService = joinRequestService;
        this.teamService = teamService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Get all join requests
     * Only administrators can see all join requests
     */
    @GetMapping
    public ResponseEntity<?> getAllJoinRequests(HttpServletRequest request) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only administrators can view all join requests");
        }
        return ResponseEntity.ok(joinRequestService.getAllJoinRequests());
    }

    /**
     * Get join request by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getJoinRequestById(@PathVariable UUID id, HttpServletRequest request) {
        Optional<JoinRequest> joinRequestOpt = joinRequestService.getJoinRequestById(id);

        if (joinRequestOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        JoinRequest joinRequest = joinRequestOpt.get();

        // Check if user is admin or team creator
        boolean isAdmin = hasAdminRole(request);
        boolean isCreator = isTeamCreator(request, joinRequest.getTeam().getCreator());

        if (!isAdmin && !isCreator) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to view this join request");
        }

        return ResponseEntity.ok(joinRequest);
    }

    /**
     * Create a new join request
     * Only the team creator can create a join request
     */
    @PostMapping
    public ResponseEntity<?> createJoinRequest(
            @RequestParam UUID teamId,
            @RequestParam UUID tournamentId,
            @RequestParam(required = false) String message,
            HttpServletRequest request) {

        // Check if the current user is the team creator
        Optional<Team> teamOpt = teamService.getTeamById(teamId);
        if (teamOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Team not found");
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
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Update join request status
     * Only tournament organizers can update the status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateJoinRequestStatus(
            @PathVariable UUID id,
            @RequestParam JoinRequest.RequestStatus status,
            @RequestParam(required = false) String responseMessage,
            HttpServletRequest request) {

        // Check if the current user has admin role
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

    /**
     * Check if the current user is the creator of the team
     */
    private boolean isTeamCreator(HttpServletRequest request, PlayerProfile creator) {
        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);
        String login = jwtUtils.extractLogin(token);
        if (login == null) {
            return false;
        }

        // Get authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        // Check if the authenticated user is the team creator
        // This is a simplified check - in a real application, you would compare user IDs
        return creator != null && creator.getEmail() != null && creator.getEmail().equals(login);
    }

    /**
     * Check if the current user has admin role
     */
    private boolean hasAdminRole(HttpServletRequest request) {
        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);
        String login = jwtUtils.extractLogin(token);
        if (login == null) {
            return false;
        }

        // Get authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        // Check if user has ADMIN role
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * Get join requests for a tournament
     * Only administrators can see all join requests for a tournament
     */
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

    /**
     * Get pending join requests for a tournament
     * Only administrators can see pending join requests for a tournament
     */
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

    /**
     * Get join requests for a team
     * Only administrators or the team creator can see join requests for a team
     */
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getJoinRequestsByTeam(@PathVariable UUID teamId, HttpServletRequest request) {
        // Check if user is admin
        boolean isAdmin = hasAdminRole(request);

        // If not admin, check if user is team creator
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

    /**
     * Delete a join request
     * Only administrators or the team creator can delete a join request
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJoinRequest(@PathVariable UUID id, HttpServletRequest request) {
        // Get the join request
        Optional<JoinRequest> joinRequestOpt = joinRequestService.getJoinRequestById(id);
        if (joinRequestOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        JoinRequest joinRequest = joinRequestOpt.get();

        // Check if user is admin
        boolean isAdmin = hasAdminRole(request);

        // If not admin, check if user is team creator
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
