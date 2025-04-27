package kz.saya.project.ascender.Services;

import kz.saya.project.ascender.Entities.JoinRequest;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Entities.Tournament;
import kz.saya.project.ascender.Repositories.JoinRequestRepository;
import kz.saya.project.ascender.Repositories.TeamRepository;
import kz.saya.project.ascender.Repositories.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JoinRequestService {

    private final JoinRequestRepository joinRequestRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;

    /**
     * Get all join requests
     */
    public List<JoinRequest> getAllJoinRequests() {
        return joinRequestRepository.findAll();
    }

    /**
     * Get join request by ID
     */
    public Optional<JoinRequest> getJoinRequestById(UUID id) {
        return joinRequestRepository.findById(id);
    }

    /**
     * Create a new join request
     */
    public JoinRequest createJoinRequest(UUID teamId, UUID tournamentId, String message) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        Optional<Tournament> tournamentOpt = tournamentRepository.findById(tournamentId);
        
        if (teamOpt.isEmpty() || tournamentOpt.isEmpty()) {
            throw new IllegalArgumentException("Team or Tournament not found");
        }
        
        // Check if a request already exists
        JoinRequest existingRequest = joinRequestRepository.findByTournamentAndTeam(tournamentOpt.get(), teamOpt.get());
        if (existingRequest != null) {
            throw new IllegalStateException("A join request already exists for this team and tournament");
        }
        
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setTeam(teamOpt.get());
        joinRequest.setTournament(tournamentOpt.get());
        joinRequest.setMessage(message);
        joinRequest.setRequestDate(OffsetDateTime.now());
        joinRequest.setStatus(JoinRequest.RequestStatus.PENDING);
        
        return joinRequestRepository.save(joinRequest);
    }

    /**
     * Update join request status
     */
    public JoinRequest updateJoinRequestStatus(UUID requestId, JoinRequest.RequestStatus status, String responseMessage) {
        Optional<JoinRequest> joinRequestOpt = joinRequestRepository.findById(requestId);
        
        if (joinRequestOpt.isEmpty()) {
            throw new IllegalArgumentException("Join request not found");
        }
        
        JoinRequest joinRequest = joinRequestOpt.get();
        joinRequest.setStatus(status);
        joinRequest.setResponseMessage(responseMessage);
        joinRequest.setResponseDate(OffsetDateTime.now());
        
        // If the request is accepted, add the team to the tournament
        if (status == JoinRequest.RequestStatus.ACCEPTED) {
            Tournament tournament = joinRequest.getTournament();
            Team team = joinRequest.getTeam();
            
            tournament.getTeams().add(team);
            tournamentRepository.save(tournament);
        }
        
        return joinRequestRepository.save(joinRequest);
    }

    /**
     * Get all join requests for a tournament
     */
    public List<JoinRequest> getJoinRequestsByTournament(UUID tournamentId) {
        Optional<Tournament> tournamentOpt = tournamentRepository.findById(tournamentId);
        
        if (tournamentOpt.isEmpty()) {
            throw new IllegalArgumentException("Tournament not found");
        }
        
        return joinRequestRepository.findByTournament(tournamentOpt.get());
    }

    /**
     * Get all join requests for a team
     */
    public List<JoinRequest> getJoinRequestsByTeam(UUID teamId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        
        if (teamOpt.isEmpty()) {
            throw new IllegalArgumentException("Team not found");
        }
        
        return joinRequestRepository.findByTeam(teamOpt.get());
    }

    /**
     * Get pending join requests for a tournament
     */
    public List<JoinRequest> getPendingJoinRequestsByTournament(UUID tournamentId) {
        Optional<Tournament> tournamentOpt = tournamentRepository.findById(tournamentId);
        
        if (tournamentOpt.isEmpty()) {
            throw new IllegalArgumentException("Tournament not found");
        }
        
        return joinRequestRepository.findByTournamentAndStatus(tournamentOpt.get(), JoinRequest.RequestStatus.PENDING);
    }

    /**
     * Delete a join request
     */
    public void deleteJoinRequest(UUID id) {
        joinRequestRepository.deleteById(id);
    }
}