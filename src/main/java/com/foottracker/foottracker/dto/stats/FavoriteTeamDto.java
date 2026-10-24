package com.foottracker.foottracker.dto.stats;

import java.util.List;

public record FavoriteTeamDto(
        Long teamId,
        String teamName,
        String crest,
        List<String> lastFiveResults,
        MatchSummaryDto lastMatch,
        MatchSummaryDto nextMatch
) {
}
