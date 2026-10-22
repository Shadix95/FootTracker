package com.foottracker.foottracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"playerId", "competitionCode"}))
@Getter
@Setter
@NoArgsConstructor
public class Scorer {

    private Long playerId;
    private String playerName;
    private String nationality;
    private String competitionCode;

    private Integer playedMatches;
    private Integer goals;
    private Integer assists;
    private Integer penalties;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Team team;
}
