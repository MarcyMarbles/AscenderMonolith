package kz.saya.project.ascender.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kz.saya.project.ascender.Enums.TechResult;
import kz.saya.sbase.Entity.MappedLocalizedClass;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "tournament_match")
public class TournamentMatch extends MappedLocalizedClass {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentTeamScore> teamScores = new ArrayList<>();

    private Integer round; // Round number in the tournament
    private String matchNumber; // Identifier for the match (e.g., "Quarter-final 1")

    @Enumerated(EnumType.STRING)
    private MatchStatus status = MatchStatus.SCHEDULED;

    private OffsetDateTime scheduledTime;
    private OffsetDateTime actualStartTime;
    private OffsetDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "match_history_id")
    private MatchHistory matchHistory; // Link to match history if available

    // Enum for match status
    public enum MatchStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    // Helper methods
    public Team getWinner() {
        if (status != MatchStatus.COMPLETED) {
            return null;
        }

        // Check for technical wins first
        Optional<TournamentTeamScore> techWinner = teamScores.stream()
                .filter(ts -> ts.getTechResult() == TechResult.WIN)
                .findFirst();

        if (techWinner.isPresent()) {
            return techWinner.get().getTeam();
        }

        // If no technical winner, get the team with the highest score
        return teamScores.stream()
                .max(Comparator.comparing(TournamentTeamScore::getScore))
                .map(TournamentTeamScore::getTeam)
                .orElse(null);
    }

    public boolean isDraw() {
        if (status != MatchStatus.COMPLETED || teamScores.isEmpty()) {
            return false;
        }

        // Check if any team has a technical win
        boolean hasTechWin = teamScores.stream()
                .anyMatch(ts -> ts.getTechResult() == TechResult.WIN);

        if (hasTechWin) {
            return false;
        }

        // Check if all teams have the same score
        Integer firstScore = teamScores.get(0).getScore();
        return teamScores.stream()
                .allMatch(ts -> ts.getScore().equals(firstScore));
    }


    public void addTeam(Team team) {
        TournamentTeamScore score = new TournamentTeamScore();
        score.setMatch(this);
        score.setTeam(team);
        teamScores.add(score);
    }

    public void updateScore(Team team, Integer score) {
        teamScores.stream()
                .filter(ts -> ts.getTeam().equals(team))
                .findFirst()
                .ifPresent(ts -> ts.setScore(score));
    }

    public void setTechResult(Team team, TechResult result) {
        teamScores.stream()
                .filter(ts -> ts.getTeam().equals(team))
                .findFirst()
                .ifPresent(ts -> ts.setTechResult(result));
    }
}
