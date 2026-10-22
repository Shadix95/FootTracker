package com.foottracker.foottracker.dto.stats;

public record RecordDto(
        int played,
        int wins,
        int draws,
        int losses,
        int goalsFor,
        int goalsAgainst,
        int points
) {
}
