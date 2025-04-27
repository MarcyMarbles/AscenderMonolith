package kz.saya.project.ascender.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TeamDTO implements Serializable {
    private UUID id;
    private OffsetDateTime created_ts;
    private OffsetDateTime updated_ts;
    private OffsetDateTime deleted_ts;
    private OffsetDateTime start_date_ts;
    private OffsetDateTime end_date_ts;
    
    private String name;
    private String description;
    
    // Instead of Set<Games>, we use Set<UUID> to reference games by their IDs
    private Set<UUID> gameIds;
    
    private String logo;
    private String background;
    private String website;
    private String discord;
    private String vk;
    private String instagram;
    private String tiktok;

}