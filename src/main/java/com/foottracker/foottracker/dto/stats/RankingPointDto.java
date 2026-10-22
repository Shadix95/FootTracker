package com.foottracker.foottracker.dto.stats;

public record RankingPointDto(
        int matchday,
        int position,
        int points
) {
}
