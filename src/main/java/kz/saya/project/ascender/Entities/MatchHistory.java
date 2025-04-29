package kz.saya.project.ascender.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kz.saya.sbasecore.Entity.MappedLocalizedClass;
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private PlayerProfile creator;

    private String gameMode;
    private String map;
    private String result;
    private String duration;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "match_history_teams",
            joinColumns = @JoinColumn(name = "match_history_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "match_history_players",
            joinColumns = @JoinColumn(name = "match_history_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<PlayerProfile> players;

    @JsonIgnore
    @OneToMany(mappedBy = "matchHistory")
    private List<TabData> tabDataList;
}
