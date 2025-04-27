package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.Entities.Scrim;
import kz.saya.project.ascender.Entities.ScrimRequest;
import kz.saya.project.ascender.Services.ScrimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/scrims")
public class ScrimController {

    private final ScrimService scrimService;

    @Autowired
    public ScrimController(ScrimService scrimService) {
        this.scrimService = scrimService;
    }

    /**
     * Get all scrims
     * @return List of all scrims
     */
    @GetMapping
    public ResponseEntity<List<Scrim>> getAllScrims() {
        return ResponseEntity.ok(scrimService.getAllScrims());
    }

    /**
     * Get scrim by ID
     * @param id Scrim ID
     * @return Scrim if found, 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Scrim> getScrimById(@PathVariable UUID id) {
        Optional<Scrim> scrim = scrimService.getScrimById(id);
        return scrim.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new scrim
     * @param scrim Scrim to create
     * @return Created scrim
     */
    @PostMapping
    public ResponseEntity<Scrim> createScrim(@RequestBody Scrim scrim) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scrimService.saveScrim(scrim));
    }

    /**
     * Update an existing scrim
     * @param id Scrim ID
     * @param scrim Updated scrim data
     * @return Updated scrim if found, 404 otherwise
     */
    @PutMapping("/{id}")
    public ResponseEntity<Scrim> updateScrim(@PathVariable UUID id, @RequestBody Scrim scrim) {
        if (!scrimService.getScrimById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        scrim.setId(id);
        return ResponseEntity.ok(scrimService.saveScrim(scrim));
    }

    /**
     * Delete a scrim
     * @param id Scrim ID
     * @return 204 No Content if successful, 404 if scrim not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScrim(@PathVariable UUID id) {
        if (!scrimService.getScrimById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        scrimService.deleteScrim(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all scrim requests
     * @return List of all scrim requests
     */
    @GetMapping("/requests")
    public ResponseEntity<List<ScrimRequest>> getAllScrimRequests() {
        return ResponseEntity.ok(scrimService.getAllScrimRequests());
    }

    /**
     * Get scrim request by ID
     * @param id Scrim request ID
     * @return Scrim request if found, 404 otherwise
     */
    @GetMapping("/requests/{id}")
    public ResponseEntity<ScrimRequest> getScrimRequestById(@PathVariable UUID id) {
        Optional<ScrimRequest> scrimRequest = scrimService.getScrimRequestById(id);
        return scrimRequest.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new scrim request
     * @param requestData Map containing name, description, gameId, and teamId
     * @return Created scrim request if successful, 400 Bad Request if invalid data
     */
    @PostMapping("/requests")
    public ResponseEntity<ScrimRequest> createScrimRequest(@RequestBody Map<String, Object> requestData) {
        String name = (String) requestData.get("name");
        String description = (String) requestData.get("description");
        UUID gameId = UUID.fromString((String) requestData.get("gameId"));
        UUID teamId = UUID.fromString((String) requestData.get("teamId"));

        Optional<ScrimRequest> scrimRequest = scrimService.createScrimRequest(name, description, gameId, teamId);
        return scrimRequest.map(request -> ResponseEntity.status(HttpStatus.CREATED).body(request))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Accept a scrim request
     * @param id Scrim request ID
     * @param requestData Map containing acceptingTeamId
     * @return Created scrim if successful, 400 Bad Request if invalid data
     */
    @PostMapping("/requests/{id}/accept")
    public ResponseEntity<Scrim> acceptScrimRequest(@PathVariable UUID id, @RequestBody Map<String, Object> requestData) {
        UUID acceptingTeamId = UUID.fromString((String) requestData.get("acceptingTeamId"));

        Optional<Scrim> scrim = scrimService.acceptScrimRequest(id, acceptingTeamId);
        return scrim.map(s -> ResponseEntity.status(HttpStatus.CREATED).body(s))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Add a player to a scrim
     * @param id Scrim ID
     * @param requestData Map containing playerId
     * @return Updated scrim if successful, 400 Bad Request if invalid data
     */
    @PostMapping("/{id}/players")
    public ResponseEntity<Scrim> addPlayerToScrim(@PathVariable UUID id, @RequestBody Map<String, Object> requestData) {
        UUID playerId = UUID.fromString((String) requestData.get("playerId"));

        Optional<Scrim> scrim = scrimService.addPlayerToScrim(id, playerId);
        return scrim.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Complete a scrim
     * @param id Scrim ID
     * @param requestData Map containing winnerTeamId, result, and duration
     * @return Updated scrim if successful, 400 Bad Request if invalid data
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Scrim> completeScrim(@PathVariable UUID id, @RequestBody Map<String, Object> requestData) {
        UUID winnerTeamId = UUID.fromString((String) requestData.get("winnerTeamId"));
        String result = (String) requestData.get("result");
        String duration = (String) requestData.get("duration");

        Optional<Scrim> scrim = scrimService.completeScrim(id, winnerTeamId, result, duration);
        return scrim.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Find active scrims by game
     * @param gameId Game ID
     * @return List of active scrims for the specified game
     */
    @GetMapping("/active/game/{gameId}")
    public ResponseEntity<List<Scrim>> findActiveScrimsByGame(@PathVariable UUID gameId) {
        return ResponseEntity.ok(scrimService.findActiveScrimsByGame(gameId));
    }

    /**
     * Find pending scrim requests by game
     * @param gameId Game ID
     * @return List of pending scrim requests for the specified game
     */
    @GetMapping("/requests/pending/game/{gameId}")
    public ResponseEntity<List<ScrimRequest>> findPendingScrimRequestsByGame(@PathVariable UUID gameId) {
        return ResponseEntity.ok(scrimService.findPendingScrimRequestsByGame(gameId));
    }
}