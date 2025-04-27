package kz.saya.project.ascender.DTO;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating a new player profile with minimal required fields.
 */
@Getter
@Setter
public class PlayerProfileCreateDTO {
    private String callingName; // Name by which the player should be called
    private String fullName; // Full name of the player
}