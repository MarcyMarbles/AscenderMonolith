package kz.saya.project.ascender.Services;

import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Repositories.PlayerProfileRepository;
import kz.saya.sbase.Entity.FileDescriptor;
import kz.saya.sbase.Service.FileDescriptorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerProfileService {

    private final PlayerProfileRepository playerProfileRepository;
    private final FileDescriptorService fileDescriptorService;

    @Autowired
    public PlayerProfileService(PlayerProfileRepository playerProfileRepository,
                                FileDescriptorService fileDescriptorService) {
        this.playerProfileRepository = playerProfileRepository;
        this.fileDescriptorService = fileDescriptorService;
    }

    /**
     * Get all player profiles
     * @return List of all player profiles
     */
    public List<PlayerProfile> getAllPlayerProfiles() {
        return playerProfileRepository.findAll();
    }

    /**
     * Get player profile by ID
     * @param id Player profile ID
     * @return Optional containing the player profile if found
     */
    public Optional<PlayerProfile> getPlayerProfileById(UUID id) {
        return playerProfileRepository.findById(id);
    }

    /**
     * Save a player profile
     * @param playerProfile Player profile to save
     * @return Saved player profile
     */
    public PlayerProfile savePlayerProfile(PlayerProfile playerProfile) {
        return playerProfileRepository.save(playerProfile);
    }

    /**
     * Delete a player profile
     * @param id Player profile ID
     */
    public void deletePlayerProfile(UUID id) {
        playerProfileRepository.deleteById(id);
    }

    /**
     * Update player profile avatar
     * @param id Player profile ID
     * @param avatarFile Avatar file
     * @return Updated player profile or empty if player profile not found
     * @throws IOException If there's an error reading the file
     */
    @Transactional
    public Optional<PlayerProfile> updateProfileAvatar(UUID id, MultipartFile avatarFile) throws IOException {
        Optional<PlayerProfile> playerProfileOpt = playerProfileRepository.findById(id);

        if (playerProfileOpt.isPresent()) {
            PlayerProfile playerProfile = playerProfileOpt.get();

            // Create a new FileDescriptor for the avatar using the FileDescriptorService
            FileDescriptor avatarDescriptor = fileDescriptorService.saveFile(avatarFile);

            // Set the avatar for the player profile
            playerProfile.setAvatar(avatarDescriptor);

            return Optional.of(playerProfileRepository.save(playerProfile));
        }

        return Optional.empty();
    }

    /**
     * Update player profile background
     * @param id Player profile ID
     * @param backgroundFile Background file
     * @return Updated player profile or empty if player profile not found
     * @throws IOException If there's an error reading the file
     */
    @Transactional
    public Optional<PlayerProfile> updateProfileBackground(UUID id, MultipartFile backgroundFile) throws IOException {
        Optional<PlayerProfile> playerProfileOpt = playerProfileRepository.findById(id);

        if (playerProfileOpt.isPresent()) {
            PlayerProfile playerProfile = playerProfileOpt.get();

            // Create a new FileDescriptor for the background using the FileDescriptorService
            FileDescriptor backgroundDescriptor = fileDescriptorService.saveFile(backgroundFile);

            // Set the background for the player profile
            playerProfile.setProfileBackground(backgroundDescriptor);

            return Optional.of(playerProfileRepository.save(playerProfile));
        }

        return Optional.empty();
    }

    /**
     * Find player profiles by skill level
     * @param skillLevel Skill level to search for
     * @return List of player profiles with the specified skill level
     */
    public List<PlayerProfile> findPlayerProfilesBySkillLevel(String skillLevel) {
        return playerProfileRepository.findAll().stream()
                .filter(profile -> skillLevel.equals(profile.getSkillLevel()))
                .toList();
    }

    /**
     * Find player profiles that are looking for a team
     * @return List of player profiles that are looking for a team
     */
    public List<PlayerProfile> findPlayerProfilesLookingForTeam() {
        return playerProfileRepository.findAll().stream()
                .filter(PlayerProfile::isLookingForTeam)
                .toList();
    }

}
