package com.foottracker.foottracker.dto.api;

public record TableRowDto(
        int position,
        TeamDto team,
        int playedGames,
        int won,
        int draw,
        int lost,
        int points,
        int goalsFor,
        int goalsAgainst,
        int goalDifference
) {
}
