package com.foottracker.foottracker.controller;

import com.foottracker.foottracker.dto.stats.HeadToHeadDto;
import com.foottracker.foottracker.dto.stats.MatchdaySummaryDto;
import com.foottracker.foottracker.dto.stats.RankingPointDto;
import com.foottracker.foottracker.dto.stats.ScorerStatsDto;
import com.foottracker.foottracker.dto.stats.StandingRowDto;
import com.foottracker.foottracker.dto.stats.TeamStatsDto;
import com.foottracker.foottracker.service.HeadToHeadService;
import com.foottracker.foottracker.service.MatchdaySummaryService;
import com.foottracker.foottracker.service.ScorerStatsService;
import com.foottracker.foottracker.service.StandingsService;
import com.foottracker.foottracker.service.TeamStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Statistiques", description = "Statistiques calculees a partir des matchs stockes en base")
@RestController
@RequestMapping("/api/competitions/{code}")
@RequiredArgsConstructor
public class StatsController {

    private final TeamStatsService teamStatsService;
    private final StandingsService standingsService;
    private final ScorerStatsService scorerStatsService;
    private final HeadToHeadService headToHeadService;
    private final MatchdaySummaryService matchdaySummaryService;

    @Operation(summary = "Classement calcule", description = "Classement final, ou apres une journee donnee si matchday est renseigne")
    @GetMapping("/standings")
    public List<StandingRowDto> standings(@Parameter(example = "FL1") @PathVariable String code,
                                          @RequestParam(required = false) Integer matchday) {
        return standingsService.getStandings(code.toUpperCase(), matchday);
    }

    @Operation(summary = "Bilan d'une equipe", description = "Moyennes de buts, clean sheets, domicile/exterieur et forme sur 5 matchs")
    @GetMapping("/teams/{teamId}/stats")
    public TeamStatsDto teamStats(@Parameter(example = "FL1") @PathVariable String code,
                                  @Parameter(example = "524") @PathVariable Long teamId) {
        return teamStatsService.getTeamStats(code.toUpperCase(), teamId);
    }

    @Operation(summary = "Evolution du classement d'une equipe journee par journee")
    @GetMapping("/teams/{teamId}/ranking-evolution")
    public List<RankingPointDto> rankingEvolution(@Parameter(example = "FL1") @PathVariable String code,
                                                  @Parameter(example = "524") @PathVariable Long teamId) {
        return standingsService.getRankingEvolution(code.toUpperCase(), teamId);
    }

    @Operation(summary = "Bilan des buteurs", description = "Buts par match, buts hors penalty et part des buts de l'equipe")
    @GetMapping("/scorers/stats")
    public List<ScorerStatsDto> scorerStats(@Parameter(example = "FL1") @PathVariable String code) {
        return scorerStatsService.getScorerStats(code.toUpperCase());
    }

    @Operation(summary = "Face-a-face entre deux equipes", description = "Bilan des confrontations directes de la saison")
    @GetMapping("/head-to-head")
    public HeadToHeadDto headToHead(@Parameter(example = "FL1") @PathVariable String code,
                                    @Parameter(example = "524") @RequestParam Long team1,
                                    @Parameter(example = "516") @RequestParam Long team2) {
        return headToHeadService.getHeadToHead(code.toUpperCase(), team1, team2);
    }

    @Operation(summary = "Bilan d'une journee", description = "Buts, victoires domicile/exterieur, plus large victoire et match le plus prolifique")
    @GetMapping("/matchdays/{matchday}/summary")
    public MatchdaySummaryDto matchdaySummary(@Parameter(example = "FL1") @PathVariable String code,
                                              @Parameter(example = "1") @PathVariable int matchday) {
        return matchdaySummaryService.getSummary(code.toUpperCase(), matchday);
    }
}
