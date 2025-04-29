package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.DTO.*;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Entities.Scrim;
import kz.saya.project.ascender.Entities.ScrimRequest;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Services.ScrimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/scrims")
@PreAuthorize("isAuthenticated()")              // все методы требуют аутентификации
@RequiredArgsConstructor
public class ScrimController {

    private final ScrimService scrimService;

    @GetMapping
    public List<ScrimDTO> list() {
        return scrimService.getAllScrims().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ScrimDTO get(@PathVariable UUID id) {
        Scrim s = scrimService.getScrimById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Scrim not found"));
        return convertToDto(s);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScrimDTO create(@RequestBody ScrimDTO dto) {
        Scrim toSave = convertToEntity(dto);
        Scrim saved = scrimService.saveScrim(toSave);
        return convertToDto(saved);
    }

    @PutMapping("/{id}")
    public ScrimDTO update(@PathVariable UUID id, @RequestBody ScrimDTO dto) {
        if (scrimService.getScrimById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scrim not found");
        }
        Scrim toSave = convertToEntity(dto);
        toSave.setId(id);
        Scrim saved = scrimService.saveScrim(toSave);
        return convertToDto(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        if (scrimService.getScrimById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scrim not found");
        }
        scrimService.deleteScrim(id);
    }


    @GetMapping("/requests")
    public List<ScrimRequestDTO> listRequests() {
        return scrimService.getAllScrimRequests().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/requests/{id}")
    public ScrimRequestDTO getRequest(@PathVariable UUID id) {
        ScrimRequest r = scrimService.getScrimRequestById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ScrimRequest not found"));
        return convertToDto(r);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ScrimRequestDTO createRequest(@RequestBody ScrimRequestDTO dto) {
        ScrimRequest r = scrimService.createScrimRequest(
                dto.getName(),
                dto.getDescription(),
                dto.getGameId(),
                dto.getTeamId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid request data"));
        return convertToDto(r);
    }

    @PostMapping("/requests/{id}/accept")
    @ResponseStatus(HttpStatus.CREATED)
    public ScrimDTO acceptRequest(
            @PathVariable UUID id,
            @RequestBody ScrimRequestAcceptDTO dto
    ) {
        Scrim s = scrimService.acceptScrimRequest(id, dto.getAcceptingTeamId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Cannot accept request"));
        return convertToDto(s);
    }

    @PostMapping("/{id}/players")
    public ScrimDTO addPlayer(
            @PathVariable UUID id,
            @RequestBody ScrimPlayerDTO dto
    ) {
        return scrimService.addPlayerToScrim(id, dto.getPlayerId())
                .map(this::convertToDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Cannot add player"));
    }

    @PostMapping("/{id}/complete")
    public ScrimDTO complete(
            @PathVariable UUID id,
            @RequestBody ScrimCompleteDTO dto
    ) {
        return scrimService.completeScrim(
                        id,
                        dto.getWinnerTeamId(),
                        dto.getResult(),
                        dto.getDuration()
                ).map(this::convertToDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Cannot complete scrim"));
    }

    @GetMapping("/active/game/{gameId}")
    public List<ScrimDTO> activeByGame(@PathVariable UUID gameId) {
        return scrimService.findActiveScrimsByGame(gameId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/requests/pending/game/{gameId}")
    public List<ScrimRequestDTO> pendingByGame(@PathVariable UUID gameId) {
        return scrimService.findPendingScrimRequestsByGame(gameId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ScrimDTO convertToDto(Scrim scrim) {
        ScrimDTO dto = new ScrimDTO();
        dto.setId(scrim.getId());
        if (scrim.getScrimRequest() != null) {
            dto.setScrimRequestId(scrim.getScrimRequest().getId());
        }
        dto.setMatchNumber(scrim.getMatchNumber());
        dto.setStatus(scrim.getStatus());
        dto.setGameMode(scrim.getGameMode());
        dto.setMap(scrim.getMap());
        dto.setResult(scrim.getResult());
        dto.setDuration(scrim.getDuration());
        dto.setMatchId(scrim.getMatchId());
        dto.setGameId(scrim.getGameId());

        if (scrim.getCreator() != null) {
            dto.setCreatorId(scrim.getCreator().getId());
        }

        if (scrim.getTeams() != null) {
            dto.setTeamIds(scrim.getTeams().stream()
                    .map(Team::getId)
                    .collect(Collectors.toSet()));
        }

        if (scrim.getPlayers() != null) {
            dto.setPlayerIds(scrim.getPlayers().stream()
                    .map(PlayerProfile::getId)
                    .collect(Collectors.toSet()));
        }

        if (scrim.getWinnerTeam() != null) {
            dto.setWinnerTeamId(scrim.getWinnerTeam().getId());
        }

        return dto;
    }

    private Scrim convertToEntity(ScrimDTO dto) {
        // This is a simplified conversion. In a real application, you would need to
        // fetch the related entities from their repositories.
        Scrim scrim = new Scrim();
        scrim.setId(dto.getId());
        scrim.setMatchNumber(dto.getMatchNumber());
        scrim.setStatus(dto.getStatus());
        scrim.setGameMode(dto.getGameMode());
        scrim.setMap(dto.getMap());
        scrim.setResult(dto.getResult());
        scrim.setDuration(dto.getDuration());
        scrim.setMatchId(dto.getMatchId());
        scrim.setGameId(dto.getGameId());

        // Note: Related entities would need to be fetched from repositories
        // This is just a placeholder for the conversion logic

        return scrim;
    }

    private ScrimRequestDTO convertToDto(ScrimRequest request) {
        ScrimRequestDTO dto = new ScrimRequestDTO();
        dto.setId(request.getId());
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());

        if (request.getGameId() != null) {
            dto.setGameId(request.getGameId().getId());
        }

        if (request.getTeamId() != null) {
            dto.setTeamId(request.getTeamId().getId());
        }

        dto.setStatus(request.getStatus());
        dto.setAcceptedAt(request.getAcceptedAt());

        if (request.getAcceptedBy() != null) {
            dto.setAcceptedById(request.getAcceptedBy().getId());
        }

        return dto;
    }
}