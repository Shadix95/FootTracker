package com.foottracker.foottracker.dto.api;

import java.util.List;

public record StandingDto(
        String stage,
        String type,
        List<TableRowDto> table
) {
}
