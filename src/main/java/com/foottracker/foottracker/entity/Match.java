package com.foottracker.foottracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
public class Match {

    private String competitionCode;
    private Integer matchday;
    private OffsetDateTime utcDate;
    private String status;

    private Integer homeScore;
    private Integer awayScore;
    private String winner;

    public boolean isHomeTeam(Long teamId) {
        return homeTeam.getId().equals(teamId);
    }
    public int goalsFor(Long teamId) {
        return isHomeTeam(teamId) ? homeScore : awayScore;
    }
    public int goalsAgainst(Long teamId) {
        return isHomeTeam(teamId) ? awayScore : homeScore;
    }

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    public String resultFor(Long teamId) {
        int scored = goalsFor(teamId);
        int conceded = goalsAgainst(teamId);
        if (scored > conceded) {
            return "W";
        }
        if (scored < conceded) {
            return "L";
        }
        return "D";
    }
}
