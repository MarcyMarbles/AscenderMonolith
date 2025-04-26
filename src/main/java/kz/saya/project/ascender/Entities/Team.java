package kz.saya.project.ascender.Entities;

import jakarta.persistence.*;
import kz.saya.sbase.Entity.MappedLocalizedClass;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "team")
public class Team extends MappedLocalizedClass {

    private String name;
    private String description;

    @ManyToMany
    @JoinTable(
            name = "team_games",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private Set<Games> games;

    private String logo;
    private String background;
    private String website;
    private String discord;
    private String vk;
    private String instagram;
    private String tiktok;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<PlayerProfile> players;
}
