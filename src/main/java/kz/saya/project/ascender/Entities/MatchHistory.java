package kz.saya.project.ascender.Entities;

import jakarta.persistence.*;
import kz.saya.sbase.Entity.MappedLocalizedClass;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "match_history")
public class MatchHistory extends MappedLocalizedClass {

    private OffsetDateTime matchDate;
    private String matchId;
    private String gameId;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private PlayerProfile creatorId;

    private String gameMode;
    private String map;
    private String result;
    private String duration;

    @ManyToMany
    @JoinTable(
            name = "match_history_teams",
            joinColumns = @JoinColumn(name = "match_history_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams;

    @ManyToMany
    @JoinTable(
            name = "match_history_players",
            joinColumns = @JoinColumn(name = "match_history_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<PlayerProfile> players;

    @OneToMany(mappedBy = "gameId")
    private List<TabData> tabDataList;
}
