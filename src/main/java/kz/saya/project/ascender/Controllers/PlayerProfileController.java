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

    /**
     * Get all player profiles
     * @return List of all player profiles
     */
    @GetMapping
    public ResponseEntity<List<PlayerProfile>> getAllPlayerProfiles() {
        return ResponseEntity.ok(playerProfileService.getAllPlayerProfiles());
    }

    /**
     * Get player profile by ID
     * @param id Player profile ID
     * @return Player profile if found, 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlayerProfile> getPlayerProfileById(@PathVariable UUID id) {
        Optional<PlayerProfile> playerProfile = playerProfileService.getPlayerProfileById(id);
        return playerProfile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new player profile
     * @param playerProfile Player profile to create
     * @return Created player profile
     */
    @PostMapping
    public ResponseEntity<PlayerProfile> createPlayerProfile(@RequestBody PlayerProfile playerProfile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playerProfileService.savePlayerProfile(playerProfile));
    }

    /**
     * Update an existing player profile
     * @param id Player profile ID
     * @param playerProfile Updated player profile data
     * @return Updated player profile if found, 404 otherwise
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlayerProfile> updatePlayerProfile(@PathVariable UUID id, @RequestBody PlayerProfile playerProfile) {
        if (!playerProfileService.getPlayerProfileById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        playerProfile.setId(id);
        return ResponseEntity.ok(playerProfileService.savePlayerProfile(playerProfile));
    }

    /**
     * Delete a player profile
     * @param id Player profile ID
     * @return 204 No Content if successful, 404 if player profile not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerProfile(@PathVariable UUID id) {
        if (!playerProfileService.getPlayerProfileById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        playerProfileService.deletePlayerProfile(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update player profile avatar
     * @param id Player profile ID
     * @param avatarFile Avatar file
     * @return Updated player profile if found, 404 otherwise
     */
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

    /**
     * Update player profile background
     * @param id Player profile ID
     * @param backgroundFile Background file
     * @return Updated player profile if found, 404 otherwise
     */
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

    /**
     * Find player profiles by skill level
     * @param skillLevel Skill level to search for
     * @return List of player profiles with the specified skill level
     */
    @GetMapping("/skill/{skillLevel}")
    public ResponseEntity<List<PlayerProfile>> findPlayerProfilesBySkillLevel(@PathVariable String skillLevel) {
        return ResponseEntity.ok(playerProfileService.findPlayerProfilesBySkillLevel(skillLevel));
    }

    /**
     * Find player profiles that are looking for a team
     * @return List of player profiles that are looking for a team
     */
    @GetMapping("/looking-for-team")
    public ResponseEntity<List<PlayerProfile>> findPlayerProfilesLookingForTeam() {
        return ResponseEntity.ok(playerProfileService.findPlayerProfilesLookingForTeam());
    }
}