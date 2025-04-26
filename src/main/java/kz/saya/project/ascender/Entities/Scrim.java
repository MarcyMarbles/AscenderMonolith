package kz.saya.project.ascender.Entities;

import jakarta.persistence.*;
import kz.saya.project.ascender.Enums.Status;
import kz.saya.sbase.Entity.MappedLocalizedClass;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "scrim")
public class Scrim extends MappedLocalizedClass {

    @ManyToOne
    @JoinColumn(name = "scrim_request_id")
    private ScrimRequest scrimRequestId;

    private int matchNumber;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private String gameMode;
    private String map;
    private String result;
    private String duration;
    private String matchId;
    private String gameId;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private PlayerProfile creatorId;

    @ManyToMany
    @JoinTable(
            name = "scrim_teams",
            joinColumns = @JoinColumn(name = "scrim_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private Set<Team> teams;

    @ManyToMany
    @JoinTable(
            name = "scrim_players",
            joinColumns = @JoinColumn(name = "scrim_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<PlayerProfile> players;

    @OneToMany(mappedBy = "gameId")
    private Set<TabData> tabDataList;

    @ManyToOne
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeamId;
}
