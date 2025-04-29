package kz.saya.project.ascender.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for adding a player to a scrim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrimPlayerDTO {
    private UUID playerId;
}