package com.foottracker.foottracker.dto.stats;

import java.util.List;

public record TeamStatsDto(
        Long teamId,
        String teamName,
        RecordDto total,
        RecordDto home,
        RecordDto away,
        double goalsForPerMatch,
        double goalsAgainstPerMatch,
        int cleanSheets,
        List<String> lastFiveResults
) {
}
