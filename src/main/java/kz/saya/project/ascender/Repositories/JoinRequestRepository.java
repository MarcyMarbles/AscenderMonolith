package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.JoinRequest;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JoinRequestRepository extends JpaRepository<JoinRequest, UUID> {
    
    // Find all requests for a specific tournament
    List<JoinRequest> findByTournament(Tournament tournament);
    
    // Find all requests from a specific team
    List<JoinRequest> findByTeam(Team team);
    
    // Find all requests with a specific status
    List<JoinRequest> findByStatus(JoinRequest.RequestStatus status);
    
    // Find requests for a tournament with a specific status
    List<JoinRequest> findByTournamentAndStatus(Tournament tournament, JoinRequest.RequestStatus status);
    
    // Find requests from a team with a specific status
    List<JoinRequest> findByTeamAndStatus(Team team, JoinRequest.RequestStatus status);
    
    // Find a specific request by tournament and team
    JoinRequest findByTournamentAndTeam(Tournament tournament, Team team);
}