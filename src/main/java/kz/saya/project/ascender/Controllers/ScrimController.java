package kz.saya.project.ascender.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import kz.saya.project.ascender.DTO.*;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Entities.Scrim;
import kz.saya.project.ascender.Entities.ScrimRequest;
import kz.saya.project.ascender.Entities.Team;
import kz.saya.project.ascender.Services.ScrimService;
import kz.saya.sbasecore.Entity.User;
import kz.saya.sbasesecurity.Service.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/scrims")
public class ScrimController extends BaseController {

    private final ScrimService scrimService;

    @Autowired
    public ScrimController(ScrimService scrimService, UserSecurityService userSecurityService) {
        super(userSecurityService);
        this.scrimService = scrimService;
    }

    @GetMapping
    public ResponseEntity<List<ScrimDTO>> getAllScrims(HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<ScrimDTO> scrimDTOs = scrimService.getAllScrims().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(scrimDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScrimDTO> getScrimById(@PathVariable UUID id, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Scrim> scrim = scrimService.getScrimById(id);
        return scrim.map(s -> ResponseEntity.ok(convertToDto(s)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ScrimDTO> createScrim(@RequestBody ScrimDTO scrimDTO, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Scrim scrim = convertToEntity(scrimDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToDto(scrimService.saveScrim(scrim)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScrimDTO> updateScrim(@PathVariable UUID id, @RequestBody ScrimDTO scrimDTO, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (!scrimService.getScrimById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Scrim scrim = convertToEntity(scrimDTO);
        scrim.setId(id);
        return ResponseEntity.ok(convertToDto(scrimService.saveScrim(scrim)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScrim(@PathVariable UUID id, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (!scrimService.getScrimById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        scrimService.deleteScrim(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ScrimRequestDTO>> getAllScrimRequests(HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<ScrimRequestDTO> requestDTOs = scrimService.getAllScrimRequests().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(requestDTOs);
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<ScrimRequestDTO> getScrimRequestById(@PathVariable UUID id, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<ScrimRequest> scrimRequest = scrimService.getScrimRequestById(id);
        return scrimRequest.map(sr -> ResponseEntity.ok(convertToDto(sr)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/requests")
    public ResponseEntity<ScrimRequestDTO> createScrimRequest(@RequestBody ScrimRequestDTO requestDTO, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<ScrimRequest> scrimRequest = scrimService.createScrimRequest(
                requestDTO.getName(),
                requestDTO.getDescription(),
                requestDTO.getGameId(),
                requestDTO.getTeamId()
        );
        
        return scrimRequest.map(sr -> ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(sr)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/requests/{id}/accept")
    public ResponseEntity<ScrimDTO> acceptScrimRequest(
            @PathVariable UUID id,
            @RequestBody ScrimRequestAcceptDTO acceptDTO,
            HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Scrim> scrim = scrimService.acceptScrimRequest(id, acceptDTO.getAcceptingTeamId());
        return scrim.map(s -> ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(s)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/{id}/players")
    public ResponseEntity<ScrimDTO> addPlayerToScrim(
            @PathVariable UUID id,
            @RequestBody ScrimPlayerDTO playerDTO,
            HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Scrim> scrim = scrimService.addPlayerToScrim(id, playerDTO.getPlayerId());
        return scrim.map(s -> ResponseEntity.ok(convertToDto(s)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ScrimDTO> completeScrim(
            @PathVariable UUID id,
            @RequestBody ScrimCompleteDTO completeDTO,
            HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Scrim> scrim = scrimService.completeScrim(
                id,
                completeDTO.getWinnerTeamId(),
                completeDTO.getResult(),
                completeDTO.getDuration()
        );
        
        return scrim.map(s -> ResponseEntity.ok(convertToDto(s)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/active/game/{gameId}")
    public ResponseEntity<List<ScrimDTO>> findActiveScrimsByGame(@PathVariable UUID gameId, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<ScrimDTO> scrimDTOs = scrimService.findActiveScrimsByGame(gameId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(scrimDTOs);
    }

    @GetMapping("/requests/pending/game/{gameId}")
    public ResponseEntity<List<ScrimRequestDTO>> findPendingScrimRequestsByGame(@PathVariable UUID gameId, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<ScrimRequestDTO> requestDTOs = scrimService.findPendingScrimRequestsByGame(gameId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(requestDTOs);
    }

    // Conversion methods
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