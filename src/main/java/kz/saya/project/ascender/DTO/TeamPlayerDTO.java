package kz.saya.project.ascender.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for adding or removing a player from a team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamPlayerDTO {
    private UUID playerId;
}