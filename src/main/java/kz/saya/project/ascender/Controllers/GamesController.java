package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Services.GamesService;
import kz.saya.sbasesecurity.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GamesController extends BaseController {

    private final GamesService gamesService;

    public GamesController(AuthService authService, GamesService gamesService) {
        super(authService);
        this.gamesService = gamesService;
    }

    @GetMapping
    public ResponseEntity<List<Games>> list() {
        return ResponseEntity.ok(gamesService.getAllGames());
    }

    /**
     * Любой может посмотреть детали
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.of(gamesService.getGameById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Games payload) {
        if (payload.getName() == null || payload.getName().isBlank()) {
            return badRequest("Game name cannot be empty");
        }
        Games saved = gamesService.saveGame(payload);
        return ResponseEntity.status(201).body(saved);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Games payload) {
        if (gamesService.getGameById(id).isEmpty()) {
            return notFound("Game not found");
        }
        payload.setId(id);
        return ResponseEntity.ok(gamesService.saveGame(payload));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (gamesService.getGameById(id).isEmpty()) {
            return notFound("Game not found");
        }
        gamesService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
