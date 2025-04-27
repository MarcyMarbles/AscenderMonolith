package kz.saya.project.ascender.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.saya.sbase.Entity.MappedSuperClass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tab_data")
public class TabData extends MappedSuperClass {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Scrim game;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "match_history_id")
    private MatchHistory matchHistory;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "player_id")
    private PlayerProfile player;

    private int kills;
    private int deaths;
    private int assists;
    private int position;
}
