package com.foottracker.foottracker.dto.stats;

import com.foottracker.foottracker.entity.Match;

import java.time.OffsetDateTime;

public record MatchSummaryDto(
        Long matchId,
        Integer matchday,
        OffsetDateTime date,
        String homeTeam,
        String awayTeam,
        Integer homeScore,
        Integer awayScore
) {

    public static MatchSummaryDto from(Match match) {
        return new MatchSummaryDto(
                match.getId(),
                match.getMatchday(),
                match.getUtcDate(),
                match.getHomeTeam().getName(),
                match.getAwayTeam().getName(),
                match.getHomeScore(),
                match.getAwayScore()
        );
    }
}
