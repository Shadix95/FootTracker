package com.foottracker.foottracker.dto.stats;

public record StandingRowDto(
        int position,
        Long teamId,
        String teamName,
        int played,
        int wins,
        int draws,
        int losses,
        int goalsFor,
        int goalsAgainst,
        int goalDifference,
        int points
) {
}
