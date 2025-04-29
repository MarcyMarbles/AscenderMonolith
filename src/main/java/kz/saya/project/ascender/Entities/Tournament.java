package kz.saya.project.ascender.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kz.saya.sbasecore.Entity.MappedLocalizedClass;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tournament")
public class Tournament extends MappedLocalizedClass {
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Games game;

    @Enumerated(EnumType.STRING)
    private TournamentFormat format; // bo1, bo3, bo5

    private Integer maxTeams;
    private Double prizePool;
    private String currency;
    private Integer maxSkill; // null if doesn't matter
    private boolean bStage; // true if it's a bracket stage

    private OffsetDateTime startDate;
    private OffsetDateTime endDate;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "tournament_teams",
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private Set<Team> teams;

    @JsonIgnore
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentMatch> matches;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status = TournamentStatus.CREATED;

    // Enum for tournament format
    public enum TournamentFormat {
        BO1("Best of 1"),
        BO3("Best of 3"),
        BO5("Best of 5");

        private final String description;

        TournamentFormat(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Enum for tournament status
    public enum TournamentStatus {
        CREATED,
        REGISTRATION_OPEN,
        REGISTRATION_CLOSED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
