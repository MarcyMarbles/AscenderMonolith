package kz.saya.project.ascender.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Services.GamesService;
import kz.saya.sbase.Security.JwtUtils;
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
@RequestMapping("/api/games")
public class GamesController {

    private final GamesService gamesService;
    private final JwtUtils jwtUtils;

    @Autowired
    public GamesController(GamesService gamesService, JwtUtils jwtUtils) {
        this.gamesService = gamesService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<List<Games>> getAllGames() {
        return ResponseEntity.ok(gamesService.getAllGames());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Games> getGameById(@PathVariable UUID id) {
        Optional<Games> game = gamesService.getGameById(id);
        return game.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Games> createGame(@RequestBody Games game, HttpServletRequest request) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(game.getName() == null || game.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gamesService.saveGame(game));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Games> updateGame(@PathVariable UUID id, @RequestBody Games game, HttpServletRequest request) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!gamesService.getGameById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        game.setId(id);
        return ResponseEntity.ok(gamesService.saveGame(game));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID id, HttpServletRequest request) {
        if (!hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!gamesService.getGameById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        gamesService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/scrimable")
    public ResponseEntity<List<Games>> getScrimableGames() {
        return ResponseEntity.ok(gamesService.getScrimableGames());
    }

    private boolean hasAdminRole(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);
        String login = jwtUtils.extractLogin(token);
        if (login == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
