package com.foottracker.foottracker.dto.stats;

import java.util.List;

public record HeadToHeadDto(
        String team1,
        String team2,
        int matchesPlayed,
        int team1Wins,
        int team2Wins,
        int draws,
        int team1Goals,
        int team2Goals,
        List<MatchSummaryDto> matches
) {
}
