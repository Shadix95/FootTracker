package com.foottracker.foottracker.dto.api;

import java.time.LocalDate;

public record SeasonDto(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        Integer currentMatchday
) {
}
