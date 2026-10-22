package com.foottracker.foottracker.dto.api;

import java.time.OffsetDateTime;

public record MatchDto(
        Long id,
        OffsetDateTime utcDate,
        String status,
        Integer matchday,
        TeamDto homeTeam,
        TeamDto awayTeam,
        ScoreDto score
) {
}
