package com.foottracker.foottracker.dto.api;

import java.util.List;

public record StandingsResponse(
        CompetitionDto competition,
        SeasonDto season,
        List<StandingDto> standings
) {
}
