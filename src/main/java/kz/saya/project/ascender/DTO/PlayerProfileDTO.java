package kz.saya.project.ascender.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for PlayerProfile entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfileDTO {
    private UUID id;
    private String nickname;
    private String skillLevel;
    private String bio;
    private boolean lookingForTeam;
    private UUID userId;
    private List<UUID> gameIds;
    private UUID avatarId;
}