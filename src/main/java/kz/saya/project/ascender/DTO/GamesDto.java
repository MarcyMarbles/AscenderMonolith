package kz.saya.project.ascender.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for {@link kz.saya.project.ascender.Entities.Games}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GamesDto implements Serializable {
    private UUID id;
    private OffsetDateTime created_ts;
    private OffsetDateTime updated_ts;
    private OffsetDateTime deleted_ts;
    private OffsetDateTime start_date_ts;
    private OffsetDateTime end_date_ts;
    private String name;
    private String description;
    private String icon;
    private String background;
    private String logo;
    private String website;
    private boolean scrimable = true;
}