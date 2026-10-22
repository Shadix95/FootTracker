package com.foottracker.foottracker.dto.api;

public record ScorerDto(
        PlayerDto player,
        TeamDto team,
        Integer playedMatches,
        Integer goals,
        Integer assists,
        Integer penalties
) {
}
