package kz.saya.project.ascender.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for completing a scrim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrimCompleteDTO {
    private UUID winnerTeamId;
    private String result;
    private String duration;
}