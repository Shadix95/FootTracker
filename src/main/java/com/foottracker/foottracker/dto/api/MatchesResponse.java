package com.foottracker.foottracker.dto.api;

import java.util.List;

public record MatchesResponse(
        CompetitionDto competition,
        List<MatchDto> matches
) {
}
