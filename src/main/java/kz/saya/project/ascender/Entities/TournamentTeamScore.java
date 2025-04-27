package kz.saya.project.ascender.Entities;

import jakarta.persistence.*;
import kz.saya.project.ascender.Enums.TechResult;
import kz.saya.sbase.Entity.MappedLocalizedClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tournament_team_score")
public class TournamentTeamScore extends MappedLocalizedClass {

    @ManyToOne
    @JoinColumn(name = "tournament_match_id")
    private TournamentMatch match;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private Integer score = 0;

    private Integer position; // Final position in the match (1st, 2nd, 3rd, etc.)

    @Enumerated(EnumType.STRING)
    private TechResult techResult = TechResult.NONE;
}
