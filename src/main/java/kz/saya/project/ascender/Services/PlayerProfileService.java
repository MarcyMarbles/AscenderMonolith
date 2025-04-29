package kz.saya.project.ascender.Services;

import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.project.ascender.Repositories.PlayerProfileRepository;
import kz.saya.sbasecore.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerProfileService {

    private final PlayerProfileRepository playerProfileRepository;

    @Autowired
    public PlayerProfileService(PlayerProfileRepository playerProfileRepository) {
        this.playerProfileRepository = playerProfileRepository;
    }

    public List<PlayerProfile> getAllPlayerProfiles() {
        return playerProfileRepository.findAll();
    }

    public Optional<PlayerProfile> getPlayerProfileById(UUID id) {
        return playerProfileRepository.findById(id);
    }

    public PlayerProfile savePlayerProfile(PlayerProfile playerProfile) {
        return playerProfileRepository.save(playerProfile);
    }

    public void deletePlayerProfile(UUID id) {
        playerProfileRepository.deleteById(id);
    }

/*    @Transactional
    public Optional<PlayerProfile> updateProfileAvatar(UUID id, MultipartFile avatarFile) throws IOException {
        Optional<PlayerProfile> playerProfileOpt = playerProfileRepository.findById(id);

        if (playerProfileOpt.isPresent()) {
            PlayerProfile playerProfile = playerProfileOpt.get();

            FileDescriptor avatarDescriptor = fileDescriptorService.saveFile(avatarFile);

            playerProfile.setAvatar(avatarDescriptor);

            return Optional.of(playerProfileRepository.save(playerProfile));
        }

        return Optional.empty();
    }

    @Transactional
    public Optional<PlayerProfile> updateProfileBackground(UUID id, MultipartFile backgroundFile) throws IOException {
        Optional<PlayerProfile> playerProfileOpt = playerProfileRepository.findById(id);

        if (playerProfileOpt.isPresent()) {
            PlayerProfile playerProfile = playerProfileOpt.get();

            FileDescriptor backgroundDescriptor = fileDescriptorService.saveFile(backgroundFile);

            playerProfile.setProfileBackground(backgroundDescriptor);

            return Optional.of(playerProfileRepository.save(playerProfile));
        }

        return Optional.empty();
    }*/

    public List<PlayerProfile> findPlayerProfilesBySkillLevel(String skillLevel) {
        return playerProfileRepository.findAll().stream()
                .filter(profile -> skillLevel.equals(profile.getSkillLevel()))
                .toList();
    }

    public List<PlayerProfile> findPlayerProfilesLookingForTeam() {
        return playerProfileRepository.findAll().stream()
                .filter(PlayerProfile::isLookingForTeam)
                .toList();
    }

    public Optional<PlayerProfile> findPlayerProfileByUser(User user) {
        return playerProfileRepository.findByUser(user);
    }

}
