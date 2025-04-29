package kz.saya.project.ascender.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for initiating a votekick
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotekickDTO {
    private UUID targetId;
}