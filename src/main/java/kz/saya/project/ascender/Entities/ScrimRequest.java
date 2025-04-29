package kz.saya.project.ascender.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.saya.sbasecore.Entity.MappedLocalizedClass;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "scrim_request")
public class ScrimRequest extends MappedLocalizedClass {

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Games gameId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team teamId;

    private String status;
    private OffsetDateTime acceptedAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "accepted_by")
    private Team acceptedBy;
}
