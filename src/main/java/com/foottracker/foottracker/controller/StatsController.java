package com.foottracker.foottracker.controller;

import com.foottracker.foottracker.dto.stats.RankingPointDto;
import com.foottracker.foottracker.dto.stats.ScorerStatsDto;
import com.foottracker.foottracker.dto.stats.StandingRowDto;
import com.foottracker.foottracker.dto.stats.TeamStatsDto;
import com.foottracker.foottracker.service.ScorerStatsService;
import com.foottracker.foottracker.service.StandingsService;
import com.foottracker.foottracker.service.TeamStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{code}")
@RequiredArgsConstructor
public class StatsController {

    private final TeamStatsService teamStatsService;
    private final StandingsService standingsService;
    private final ScorerStatsService scorerStatsService;

    @GetMapping("/standings")
    public List<StandingRowDto> standings(@PathVariable String code,
                                          @RequestParam(required = false) Integer matchday) {
        return standingsService.getStandings(code.toUpperCase(), matchday);
    }

    @GetMapping("/teams/{teamId}/stats")
    public TeamStatsDto teamStats(@PathVariable String code, @PathVariable Long teamId) {
        return teamStatsService.getTeamStats(code.toUpperCase(), teamId);
    }

    @GetMapping("/teams/{teamId}/ranking-evolution")
    public List<RankingPointDto> rankingEvolution(@PathVariable String code, @PathVariable Long teamId) {
        return standingsService.getRankingEvolution(code.toUpperCase(), teamId);
    }

    @GetMapping("/scorers/stats")
    public List<ScorerStatsDto> scorerStats(@PathVariable String code) {
        return scorerStatsService.getScorerStats(code.toUpperCase());
    }
}
