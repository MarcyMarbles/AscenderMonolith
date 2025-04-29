package kz.saya.project.ascender.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for accepting a scrim request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrimRequestAcceptDTO {
    private UUID acceptingTeamId;
}