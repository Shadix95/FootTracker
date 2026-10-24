package com.foottracker.foottracker.dto.stats;

import java.util.List;

public record MatchdaySummaryDto(
        int matchday,
        int matchesPlayed,
        int matchesRemaining,
        int totalGoals,
        double goalsPerMatch,
        int homeWins,
        int awayWins,
        int draws,
        MatchSummaryDto biggestWin,
        MatchSummaryDto highestScoringMatch,
        List<MatchSummaryDto> matches
) {
}
