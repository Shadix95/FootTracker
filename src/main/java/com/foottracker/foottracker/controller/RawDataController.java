package com.foottracker.foottracker.controller;

import com.foottracker.foottracker.client.FootballDataClient;
import com.foottracker.foottracker.dto.api.MatchesResponse;
import com.foottracker.foottracker.dto.api.ScorersResponse;
import com.foottracker.foottracker.dto.api.StandingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/raw/competitions/{code}")
@RequiredArgsConstructor
public class RawDataController {

    private final FootballDataClient footballDataClient;

    @GetMapping("/standings")
    public StandingsResponse standings(@PathVariable String code) {
        return footballDataClient.getStandings(code);
    }

    @GetMapping("/matches")
    public MatchesResponse matches(@PathVariable String code) {
        return footballDataClient.getMatches(code);
    }

    @GetMapping("/scorers")
    public ScorersResponse scorers(@PathVariable String code) {
        return footballDataClient.getScorers(code);
    }
}
