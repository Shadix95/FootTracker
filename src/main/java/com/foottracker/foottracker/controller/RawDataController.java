package com.foottracker.foottracker.controller;

import com.foottracker.foottracker.client.FootballDataClient;
import com.foottracker.foottracker.dto.api.MatchesResponse;
import com.foottracker.foottracker.dto.api.ScorersResponse;
import com.foottracker.foottracker.dto.api.StandingsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Donnees brutes", description = "Reponses de football-data.org sans traitement (mises en cache 5 min)")
@RestController
@RequestMapping("/api/raw/competitions/{code}")
@RequiredArgsConstructor
public class RawDataController {

    private final FootballDataClient footballDataClient;

    @Operation(summary = "Classement officiel")
    @GetMapping("/standings")
    public StandingsResponse standings(@Parameter(example = "FL1") @PathVariable String code) {
        return footballDataClient.getStandings(code);
    }

    @Operation(summary = "Matchs de la saison")
    @GetMapping("/matches")
    public MatchesResponse matches(@Parameter(example = "FL1") @PathVariable String code) {
        return footballDataClient.getMatches(code);
    }

    @Operation(summary = "Meilleurs buteurs")
    @GetMapping("/scorers")
    public ScorersResponse scorers(@Parameter(example = "FL1") @PathVariable String code) {
        return footballDataClient.getScorers(code);
    }
}
