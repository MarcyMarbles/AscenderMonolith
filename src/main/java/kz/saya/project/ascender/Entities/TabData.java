package kz.saya.project.ascender.Entities;

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

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Scrim gameId;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private PlayerProfile playerId;

    private int kills;
    private int deaths;
    private int assists;
    private int position;
}
