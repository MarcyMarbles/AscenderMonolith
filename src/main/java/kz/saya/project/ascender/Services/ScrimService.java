package kz.saya.project.ascender.Services;

import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Entities.Scrim;
import kz.saya.project.ascender.Entities.ScrimRequest;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Enums.Status;
import kz.saya.project.ascender.Repositories.GamesRepository;
import kz.saya.project.ascender.Repositories.PlayerProfileRepository;
import kz.saya.project.ascender.Repositories.ScrimRepository;
import kz.saya.project.ascender.Repositories.ScrimRequestRepository;
import kz.saya.project.ascender.Repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScrimService {

    private final ScrimRepository scrimRepository;
    private final ScrimRequestRepository scrimRequestRepository;
    private final TeamRepository teamRepository;
    private final PlayerProfileRepository playerProfileRepository;
    private final GamesRepository gamesRepository;

    @Autowired
    public ScrimService(ScrimRepository scrimRepository, 
                        ScrimRequestRepository scrimRequestRepository,
                        TeamRepository teamRepository,
                        PlayerProfileRepository playerProfileRepository,
                        GamesRepository gamesRepository) {
        this.scrimRepository = scrimRepository;
        this.scrimRequestRepository = scrimRequestRepository;
        this.teamRepository = teamRepository;
        this.playerProfileRepository = playerProfileRepository;
        this.gamesRepository = gamesRepository;
    }

    public List<Scrim> getAllScrims() {
        return scrimRepository.findAll();
    }

    public Optional<Scrim> getScrimById(UUID id) {
        return scrimRepository.findById(id);
    }

    public Scrim saveScrim(Scrim scrim) {
        return scrimRepository.save(scrim);
    }

    public void deleteScrim(UUID id) {
        scrimRepository.deleteById(id);
    }

    public List<ScrimRequest> getAllScrimRequests() {
        return scrimRequestRepository.findAll();
    }

    public Optional<ScrimRequest> getScrimRequestById(UUID id) {
        return scrimRequestRepository.findById(id);
    }

    public ScrimRequest saveScrimRequest(ScrimRequest scrimRequest) {
        return scrimRequestRepository.save(scrimRequest);
    }

    public void deleteScrimRequest(UUID id) {
        scrimRequestRepository.deleteById(id);
    }

    public Optional<ScrimRequest> createScrimRequest(String name, String description, UUID gameId, UUID teamId) {
        Optional<Games> gameOpt = gamesRepository.findById(gameId);
        Optional<Team> teamOpt = teamRepository.findById(teamId);

        if (gameOpt.isPresent() && teamOpt.isPresent()) {
            Games game = gameOpt.get();
            Team team = teamOpt.get();

            if (!game.isScrimable()) {
                return Optional.empty();
            }

            ScrimRequest scrimRequest = new ScrimRequest();
            scrimRequest.setName(name);
            scrimRequest.setDescription(description);
            scrimRequest.setGameId(game);
            scrimRequest.setTeamId(team);
            scrimRequest.setStatus("PENDING");

            return Optional.of(scrimRequestRepository.save(scrimRequest));
        }

        return Optional.empty();
    }

    public Optional<Scrim> acceptScrimRequest(UUID scrimRequestId, UUID acceptingTeamId) {
        Optional<ScrimRequest> scrimRequestOpt = scrimRequestRepository.findById(scrimRequestId);
        Optional<Team> acceptingTeamOpt = teamRepository.findById(acceptingTeamId);

        if (scrimRequestOpt.isPresent() && acceptingTeamOpt.isPresent()) {
            ScrimRequest scrimRequest = scrimRequestOpt.get();
            Team acceptingTeam = acceptingTeamOpt.get();

            if ("ACCEPTED".equals(scrimRequest.getStatus())) {
                return Optional.empty();
            }

            scrimRequest.setStatus("ACCEPTED");
            scrimRequest.setAcceptedAt(OffsetDateTime.now());
            scrimRequest.setAcceptedBy(acceptingTeam);
            scrimRequestRepository.save(scrimRequest);

            Scrim scrim = new Scrim();
            scrim.setScrimRequest(scrimRequest);
            scrim.setMatchNumber(1);
            scrim.setStatus(Status.ACTIVE);
            scrim.setCreator(scrimRequest.getTeamId().getPlayers().get(0));

            Set<Team> teams = new HashSet<>();
            teams.add(scrimRequest.getTeamId());
            teams.add(acceptingTeam);
            scrim.setTeams(teams);

            return Optional.of(scrimRepository.save(scrim));
        }

        return Optional.empty();
    }

    public Optional<Scrim> addPlayerToScrim(UUID scrimId, UUID playerId) {
        Optional<Scrim> scrimOpt = scrimRepository.findById(scrimId);
        Optional<PlayerProfile> playerOpt = playerProfileRepository.findById(playerId);

        if (scrimOpt.isPresent() && playerOpt.isPresent()) {
            Scrim scrim = scrimOpt.get();
            PlayerProfile player = playerOpt.get();

            if (scrim.getStatus() != Status.ACTIVE) {
                return Optional.empty();
            }

            Set<PlayerProfile> players = scrim.getPlayers();
            if (players == null) {
                players = new HashSet<>();
            }
            players.add(player);
            scrim.setPlayers(players);

            return Optional.of(scrimRepository.save(scrim));
        }

        return Optional.empty();
    }

    public Optional<Scrim> completeScrim(UUID scrimId, UUID winnerTeamId, String result, String duration) {
        Optional<Scrim> scrimOpt = scrimRepository.findById(scrimId);
        Optional<Team> winnerTeamOpt = teamRepository.findById(winnerTeamId);

        if (scrimOpt.isPresent() && winnerTeamOpt.isPresent()) {
            Scrim scrim = scrimOpt.get();
            Team winnerTeam = winnerTeamOpt.get();

            if (scrim.getStatus() != Status.ACTIVE) {
                return Optional.empty();
            }

            if (!scrim.getTeams().contains(winnerTeam)) {
                return Optional.empty();
            }

            scrim.setStatus(Status.END);
            scrim.setWinnerTeam(winnerTeam);
            scrim.setResult(result);
            scrim.setDuration(duration);

            return Optional.of(scrimRepository.save(scrim));
        }

        return Optional.empty();
    }

    public List<Scrim> findActiveScrimsByGame(UUID gameId) {
        return scrimRepository.findAll().stream()
                .filter(scrim -> scrim.getStatus() == Status.ACTIVE)
                .filter(scrim -> scrim.getScrimRequest().getGameId().getId().equals(gameId))
                .collect(Collectors.toList());
    }

    public List<ScrimRequest> findPendingScrimRequestsByGame(UUID gameId) {
        return scrimRequestRepository.findAll().stream()
                .filter(request -> "PENDING".equals(request.getStatus()))
                .filter(request -> request.getGameId().getId().equals(gameId))
                .collect(Collectors.toList());
    }
}
