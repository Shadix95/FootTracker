package com.foottracker.foottracker.dto.api;

import java.util.List;

public record ScorersResponse(
        CompetitionDto competition,
        SeasonDto season,
        List<ScorerDto> scorers
) {
}
