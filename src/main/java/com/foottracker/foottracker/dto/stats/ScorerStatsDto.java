package com.foottracker.foottracker.dto.stats;

public record ScorerStatsDto(
        String playerName,
        String teamName,
        int playedMatches,
        int goals,
        int penalties,
        int nonPenaltyGoals,
        double goalsPerMatch,
        int teamGoals,
        double shareOfTeamGoals
) {
}
