package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Services.PlayerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/players")
public class PlayerProfileController {

    private final PlayerProfileService playerProfileService;

    @Autowired
    public PlayerProfileController(PlayerProfileService playerProfileService) {
        this.playerProfileService = playerProfileService;
    }

    @GetMapping
    public ResponseEntity<List<PlayerProfile>> getAllPlayerProfiles() {
        return ResponseEntity.ok(playerProfileService.getAllPlayerProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerProfile> getPlayerProfileById(@PathVariable UUID id) {
        Optional<PlayerProfile> playerProfile = playerProfileService.getPlayerProfileById(id);
        return playerProfile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlayerProfile> createPlayerProfile(@RequestBody PlayerProfile playerProfile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playerProfileService.savePlayerProfile(playerProfile));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerProfile> updatePlayerProfile(@PathVariable UUID id, @RequestBody PlayerProfile playerProfile) {
        if (!playerProfileService.getPlayerProfileById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        playerProfile.setId(id);
        return ResponseEntity.ok(playerProfileService.savePlayerProfile(playerProfile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerProfile(@PathVariable UUID id) {
        if (!playerProfileService.getPlayerProfileById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        playerProfileService.deletePlayerProfile(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlayerProfile> updateProfileAvatar(@PathVariable UUID id, @RequestParam("file") MultipartFile avatarFile) {
        try {
            Optional<PlayerProfile> updatedProfile = playerProfileService.updateProfileAvatar(id, avatarFile);
            return updatedProfile.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/{id}/background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlayerProfile> updateProfileBackground(@PathVariable UUID id, @RequestParam("file") MultipartFile backgroundFile) {
        try {
            Optional<PlayerProfile> updatedProfile = playerProfileService.updateProfileBackground(id, backgroundFile);
            return updatedProfile.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/skill/{skillLevel}")
    public ResponseEntity<List<PlayerProfile>> findPlayerProfilesBySkillLevel(@PathVariable String skillLevel) {
        return ResponseEntity.ok(playerProfileService.findPlayerProfilesBySkillLevel(skillLevel));
    }

    @GetMapping("/looking-for-team")
    public ResponseEntity<List<PlayerProfile>> findPlayerProfilesLookingForTeam() {
        return ResponseEntity.ok(playerProfileService.findPlayerProfilesLookingForTeam());
    }
}
