package kz.saya.project.ascender.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import kz.saya.project.ascender.DTO.PlayerProfileCreateDTO;
import kz.saya.project.ascender.DTO.PlayerProfileEditDTO;
import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Repositories.GamesRepository;
import kz.saya.project.ascender.Services.PlayerProfileService;
import kz.saya.sbase.Entity.FileDescriptor;
import kz.saya.sbase.Entity.User;
import kz.saya.sbase.Service.FileDescriptorService;
import kz.saya.sbase.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/players")
public class PlayerProfileController {

    private final PlayerProfileService playerProfileService;
    private final GamesRepository gamesRepository;
    private final FileDescriptorService fileDescriptorService;
    private final UserService userService;

    @Autowired
    public PlayerProfileController(PlayerProfileService playerProfileService,
                                   GamesRepository gamesRepository,
                                   FileDescriptorService fileDescriptorService, UserService userService) {
        this.playerProfileService = playerProfileService;
        this.gamesRepository = gamesRepository;
        this.fileDescriptorService = fileDescriptorService;
        this.userService = userService;
    }

    private User extractUserFromToken(HttpServletRequest request) {
        return userService.extractUserFromToken(request.getHeader("Authorization"));
    }

    @GetMapping
    public ResponseEntity<List<PlayerProfile>> getAllPlayerProfiles() {
        return ResponseEntity.ok(playerProfileService.getAllPlayerProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerProfile> getPlayerProfileById(@PathVariable UUID id, HttpServletRequest request) {
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.getPlayerProfileById(id);
        if (playerProfileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerProfile playerProfile = playerProfileOpt.get();
        if (!playerProfile.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(playerProfile);
    }

    @PostMapping
    public ResponseEntity<PlayerProfile> createPlayerProfile(@RequestBody PlayerProfileCreateDTO createDTO, HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setCallingName(createDTO.getCallingName());
        playerProfile.setFullName(createDTO.getFullName());
        playerProfile.setUser(user);

        // Save the entity
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playerProfileService.savePlayerProfile(playerProfile));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerProfile> updatePlayerProfile(@PathVariable UUID id,
                                                             @RequestBody PlayerProfileEditDTO editDTO,
                                                             HttpServletRequest request) {
        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<PlayerProfile> existingProfileOpt = playerProfileService.getPlayerProfileById(id);
        if (existingProfileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PlayerProfile existingProfile = existingProfileOpt.get();
        if (!existingProfile.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        existingProfile.setCallingName(editDTO.getCallingName());
        existingProfile.setFullName(editDTO.getFullName());
        existingProfile.setEmail(editDTO.getEmail());
        existingProfile.setSteamId(editDTO.getSteamId());
        existingProfile.setDiscordId(editDTO.getDiscordId());
        existingProfile.setTwitchUsername(editDTO.getTwitchUsername());
        existingProfile.setYoutubeChannel(editDTO.getYoutubeChannel());
        existingProfile.setBio(editDTO.getBio());
        existingProfile.setBirthDate(editDTO.getBirthDate());
        existingProfile.setCountry(editDTO.getCountry());
        existingProfile.setCity(editDTO.getCity());
        existingProfile.setLanguage(editDTO.getLanguage());
        existingProfile.setSkillLevel(editDTO.getSkillLevel());
        existingProfile.setTotalMatchesPlayed(editDTO.getTotalMatchesPlayed());
        existingProfile.setTotalWins(editDTO.getTotalWins());
        existingProfile.setWinRate(editDTO.getWinRate());
        existingProfile.setLookingForTeam(editDTO.isLookingForTeam());
        existingProfile.setAvailability(editDTO.getAvailability());
        existingProfile.setTimezone(editDTO.getTimezone());

        // Update preferred games if provided
        if (editDTO.getPreferredGameIds() != null && !editDTO.getPreferredGameIds().isEmpty()) {
            java.util.Set<Games> games = new java.util.HashSet<>();
            for (UUID gameId : editDTO.getPreferredGameIds()) {
                gamesRepository.findById(gameId).ifPresent(games::add);
            }
            existingProfile.setPreferredGames(games);
        }

        if (editDTO.getAchievements() != null) {
            existingProfile.setAchievements(editDTO.getAchievements());
        }

        return ResponseEntity.ok(playerProfileService.savePlayerProfile(existingProfile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerProfile(@PathVariable UUID id, HttpServletRequest request) {
        Optional<PlayerProfile> playerProfileOpt = playerProfileService.getPlayerProfileById(id);
        if (playerProfileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = extractUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerProfile playerProfile = playerProfileOpt.get();
        if (!playerProfile.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        playerProfileService.deletePlayerProfile(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlayerProfile> updateProfileAvatar(@PathVariable UUID id, @RequestParam("file") MultipartFile avatarFile, HttpServletRequest request) {
        try {
            Optional<PlayerProfile> playerProfileOpt = playerProfileService.getPlayerProfileById(id);
            if (playerProfileOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = extractUserFromToken(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            PlayerProfile playerProfile = playerProfileOpt.get();
            if (!playerProfile.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Optional<PlayerProfile> updatedProfile = playerProfileService.updateProfileAvatar(id, avatarFile);
            return updatedProfile.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/{id}/background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlayerProfile> updateProfileBackground(@PathVariable UUID id, @RequestParam("file") MultipartFile backgroundFile, HttpServletRequest request) {
        try {
            Optional<PlayerProfile> playerProfileOpt = playerProfileService.getPlayerProfileById(id);
            if (playerProfileOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = extractUserFromToken(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            PlayerProfile playerProfile = playerProfileOpt.get();
            if (!playerProfile.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

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
