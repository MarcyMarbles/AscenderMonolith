package kz.saya.project.ascender.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for editing an existing player profile with all available fields.
 */
@Getter
@Setter
public class PlayerProfileEditDTO {
    private String callingName;
    private String fullName;
    private String email;
    private String steamId;
    private String discordId;
    private String twitchUsername;
    private String youtubeChannel;
    private String bio;
    private LocalDate birthDate;
    private String country;
    private String city;
    private String language;
    private UUID avatarId;
    private UUID profileBackgroundId;
    private String skillLevel;
    private Integer totalMatchesPlayed;
    private Integer totalWins;
    private Double winRate;
    private Set<UUID> preferredGameIds;
    private Set<String> achievements;
    private boolean lookingForTeam;
    private String availability;
    private String timezone;
}