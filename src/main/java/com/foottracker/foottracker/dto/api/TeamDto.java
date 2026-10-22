package com.foottracker.foottracker.dto.api;

public record TeamDto(
        Long id,
        String name,
        String shortName,
        String tla,
        String crest
) {
}
