package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Services.GamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GamesController {

    private final GamesService gamesService;

    @Autowired
    public GamesController(GamesService gamesService) {
        this.gamesService = gamesService;
    }

    /**
     * Get all games
     * @return List of all games
     */
    @GetMapping
    public ResponseEntity<List<Games>> getAllGames() {
        return ResponseEntity.ok(gamesService.getAllGames());
    }

    /**
     * Get game by ID
     * @param id Game ID
     * @return Game if found, 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Games> getGameById(@PathVariable UUID id) {
        Optional<Games> game = gamesService.getGameById(id);
        return game.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new game
     * @param game Game to create
     * @return Created game
     */
    @PostMapping
    public ResponseEntity<Games> createGame(@RequestBody Games game) {
        if(game.getName() == null || game.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gamesService.saveGame(game));
    }

    /**
     * Update an existing game
     * @param id Game ID
     * @param game Updated game data
     * @return Updated game if found, 404 otherwise
     */
    @PutMapping("/{id}")
    public ResponseEntity<Games> updateGame(@PathVariable UUID id, @RequestBody Games game) {
        if (!gamesService.getGameById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        game.setId(id);
        return ResponseEntity.ok(gamesService.saveGame(game));
    }

    /**
     * Delete a game
     * @param id Game ID
     * @return 204 No Content if successful, 404 if game not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID id) {
        if (!gamesService.getGameById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        gamesService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all games that support scrims
     * @return List of games that support scrims
     */
    @GetMapping("/scrimable")
    public ResponseEntity<List<Games>> getScrimableGames() {
        return ResponseEntity.ok(gamesService.getScrimableGames());
    }
}