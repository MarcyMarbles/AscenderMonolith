package kz.saya.project.ascender.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for ScrimRequest entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrimRequestDTO {
    private UUID id;
    private String name;
    private String description;
    private UUID gameId;
    private UUID teamId;
    private String status;
    private OffsetDateTime acceptedAt;
    private UUID acceptedById;
}