package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.DTO.PlayerProfileCreateDTO;
import kz.saya.project.ascender.DTO.PlayerProfileEditDTO;
import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Repositories.GamesRepository;
import kz.saya.project.ascender.Services.PlayerProfileService;
import kz.saya.sbasecore.Entity.User;
import kz.saya.sbasesecurity.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/players")
public class PlayerProfileController extends BaseController {

    private final PlayerProfileService playerProfileService;
    private final GamesRepository gamesRepository;

    public PlayerProfileController(AuthService authService,
                                   PlayerProfileService playerProfileService,
                                   GamesRepository gamesRepository) {
        super(authService);
        this.playerProfileService = playerProfileService;
        this.gamesRepository = gamesRepository;
    }

    @GetMapping
    public ResponseEntity<List<PlayerProfile>> list() {
        // Можно, по желанию, требовать аутентификацию: if (currentUser()==null) ...
        return ResponseEntity.ok(playerProfileService.getAllPlayerProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerProfile> getById(@PathVariable UUID id) {
        User user = currentUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        PlayerProfile profile = playerProfileService.getPlayerProfileById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player profile not found"));

        if (!profile.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to view this profile");
        }

        return ResponseEntity.ok(profile);
    }

    @PostMapping
    public ResponseEntity<PlayerProfile> create(@RequestBody PlayerProfileCreateDTO dto) {
        User user = currentUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        PlayerProfile profile = new PlayerProfile();
        profile.setCallingName(dto.getCallingName());
        profile.setFullName(dto.getFullName());
        profile.setUser(user);

        PlayerProfile saved = playerProfileService.savePlayerProfile(profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerProfile> update(@PathVariable UUID id,
                                                @RequestBody PlayerProfileEditDTO dto) {
        User user = currentUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        PlayerProfile profile = playerProfileService.getPlayerProfileById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player profile not found"));

        if (!profile.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this profile");
        }

        profile.setCallingName(dto.getCallingName());
        profile.setFullName(dto.getFullName());
        profile.setEmail(dto.getEmail());
        // … обновите по аналогии остальные поля из dto …

        // Обновление preferredGames
        if (dto.getPreferredGameIds() != null) {
            Set<Games> games = new HashSet<>();
            dto.getPreferredGameIds().forEach(gid ->
                    gamesRepository.findById(gid).ifPresent(games::add)
            );
            profile.setPreferredGames(games);
        }

        PlayerProfile updated = playerProfileService.savePlayerProfile(profile);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        PlayerProfile profile = playerProfileService.getPlayerProfileById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player profile not found"));

        User user = currentUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        if (!profile.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this profile");
        }

        playerProfileService.deletePlayerProfile(id);
        return ResponseEntity.noContent().build();
    }
}
