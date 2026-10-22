package com.foottracker.foottracker.dto.api;

public record ScoreDto(
        String winner,
        ScoreDetailDto fullTime
) {
}
