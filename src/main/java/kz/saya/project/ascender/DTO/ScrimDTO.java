package kz.saya.project.ascender.DTO;

import kz.saya.project.ascender.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object for Scrim entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrimDTO {
    private UUID id;
    private UUID scrimRequestId;
    private int matchNumber;
    private Status status;
    private String gameMode;
    private String map;
    private String result;
    private String duration;
    private String matchId;
    private String gameId;
    private UUID creatorId;
    private Set<UUID> teamIds;
    private Set<UUID> playerIds;
    private UUID winnerTeamId;
    
    // Fields for creating a new scrim
    private String name;
    private String description;
}