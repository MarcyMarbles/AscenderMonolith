package kz.saya.project.ascender.Controllers;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Entities.Tournament;
import kz.saya.project.ascender.Entities.TournamentMatch;
import kz.saya.project.ascender.Services.TournamentService;
import kz.saya.project.ascender.Enums.TechResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments")
@Validated
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Tournament> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String game,
            @RequestParam(required = false) Tournament.TournamentFormat format,
            @RequestParam(required = false) Tournament.TournamentStatus status
    ) {
        return tournamentService.getAllTournaments().stream()
                .filter(t -> name   == null || t.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(t -> game   == null || (t.getGame() != null && t.getGame().getName().toLowerCase().contains(game.toLowerCase())))
                .filter(t -> format == null || format == t.getFormat())
                .filter(t -> status == null || status == t.getStatus())
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Tournament getById(@PathVariable UUID id) {
        return tournamentService.getTournamentById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tournament not found with id " + id
                ));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Tournament create(@Valid @RequestBody TournamentDto dto) {
        Tournament t = mapToEntity(dto);
        return tournamentService.saveTournament(t);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Tournament update(
            @PathVariable UUID id,
            @Valid @RequestBody TournamentDto dto
    ) {
        Tournament existing = tournamentService.getTournamentById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tournament not found with id " + id
                ));
        updateEntity(existing, dto);
        return tournamentService.saveTournament(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        if (tournamentService.getTournamentById(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tournament not found with id " + id
            );
        }
        tournamentService.deleteTournament(id);
    }

    @PostMapping("/{tournamentId}/teams")
    @PreAuthorize("hasRole('ADMIN')")
    public Tournament addTeam(
            @PathVariable UUID tournamentId,
            @Valid @RequestBody IdDto dto
    ) {
        return tournamentService.addTeamToTournament(tournamentId, dto.getId());
    }

    @DeleteMapping("/{tournamentId}/teams/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTeam(
            @PathVariable UUID tournamentId,
            @PathVariable UUID teamId
    ) {
        tournamentService.removeTeamFromTournament(tournamentId, teamId);
    }

    @PostMapping("/{tournamentId}/matches")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public TournamentMatch createMatch(
            @PathVariable UUID tournamentId,
            @Valid @RequestBody MatchDto dto
    ) {
        return tournamentService.createMatch(
                tournamentId,
                dto.getTeamIds(),
                dto.getRound(),
                dto.getMatchNumber()
        );
    }

    @PutMapping("/matches/{matchId}/scores")
    @PreAuthorize("hasRole('ADMIN')")
    public TournamentMatch updateScores(
            @PathVariable UUID matchId,
            @Valid @RequestBody ScoresDto dto
    ) {
        return tournamentService.updateMatchScore(matchId, dto.getScores());
    }

    @PutMapping("/matches/{matchId}/tech-result")
    @PreAuthorize("hasRole('ADMIN')")
    public TournamentMatch setTechResult(
            @PathVariable UUID matchId,
            @Valid @RequestBody TechResultDto dto
    ) {
        return tournamentService.setTechnicalResult(
                matchId, dto.getTeamId(), dto.getTechResult()
        );
    }

    @PostMapping("/{tournamentId}/bracket")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TournamentMatch> generateBracket(@PathVariable UUID tournamentId) {
        return tournamentService.generateBracket(tournamentId);
    }

    @PutMapping("/{tournamentId}/bracket")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateBracket(@PathVariable UUID tournamentId) {
        tournamentService.updateBracket(tournamentId);
    }


    private Tournament mapToEntity(TournamentDto dto) {
        Tournament t = new Tournament();
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        t.setFormat(dto.getFormat());
        t.setMaxTeams(dto.getMaxTeams());
        t.setPrizePool(dto.getPrizePool());
        t.setCurrency(dto.getCurrency());
        t.setMaxSkill(dto.getMaxSkill());
        t.setBStage(dto.isBStage());
        t.setStatus(dto.getStatus());
        t.setStartDate(dto.getStartDate());
        t.setEndDate(dto.getEndDate());
        if (dto.getGameId() != null) {
            Games g = new Games(); g.setId(dto.getGameId());
            t.setGame(g);
        }
        return t;
    }

    private void updateEntity(Tournament t, TournamentDto dto) {
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        t.setFormat(dto.getFormat());
        t.setMaxTeams(dto.getMaxTeams());
        t.setPrizePool(dto.getPrizePool());
        t.setCurrency(dto.getCurrency());
        t.setMaxSkill(dto.getMaxSkill());
        t.setBStage(dto.isBStage());
        t.setStatus(dto.getStatus());
        t.setStartDate(dto.getStartDate());
        t.setEndDate(dto.getEndDate());
        if (dto.getGameId() != null) {
            Games g = new Games(); g.setId(dto.getGameId());
            t.setGame(g);
        } else {
            t.setGame(null);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentDto {
        @NotBlank
        private String name;
        private String description;
        @NotNull
        private UUID gameId;
        private Tournament.TournamentFormat format;
        private Integer maxTeams;
        private Double prizePool;
        private String currency;
        private Integer maxSkill;
        private boolean bStage;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
        private Tournament.TournamentStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdDto {
        @NotNull
        private UUID id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchDto {
        @NotEmpty
        private List<@NotNull UUID> teamIds;
        @NotNull
        private Integer round;
        @NotBlank
        private String matchNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoresDto {
        @NotEmpty
        private Map<@NotNull UUID, @NotNull @Min(0) Integer> scores;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechResultDto {
        @NotNull
        private UUID teamId;
        @NotNull
        private TechResult techResult;
    }
}
