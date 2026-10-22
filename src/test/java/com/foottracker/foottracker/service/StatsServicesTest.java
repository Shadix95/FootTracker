package com.foottracker.foottracker.service;

import com.foottracker.foottracker.dto.stats.RankingPointDto;
import com.foottracker.foottracker.dto.stats.ScorerStatsDto;
import com.foottracker.foottracker.dto.stats.StandingRowDto;
import com.foottracker.foottracker.dto.stats.TeamStatsDto;
import com.foottracker.foottracker.entity.Match;
import com.foottracker.foottracker.entity.Scorer;
import com.foottracker.foottracker.entity.Team;
import com.foottracker.foottracker.repository.MatchRepository;
import com.foottracker.foottracker.repository.ScorerRepository;
import com.foottracker.foottracker.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:statstest")
class StatsServicesTest {

    private static final String CODE = "FL1";

    @Autowired
    private TeamStatsService teamStatsService;

    @Autowired
    private StandingsService standingsService;

    @Autowired
    private ScorerStatsService scorerStatsService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ScorerRepository scorerRepository;

    private Team psg;
    private Team om;
    private Team ol;

    @BeforeEach
    void setUp() {
        scorerRepository.deleteAll();
        matchRepository.deleteAll();
        teamRepository.deleteAll();

        psg = saveTeam(1L, "PSG");
        om = saveTeam(2L, "OM");
        ol = saveTeam(3L, "OL");

        saveMatch(10L, 1, psg, om, 2, 0, "FINISHED");
        saveMatch(11L, 2, ol, psg, 1, 1, "FINISHED");
        saveMatch(12L, 3, om, ol, 3, 1, "FINISHED");
        saveMatch(13L, 4, om, psg, 1, 0, "FINISHED");
        saveMatch(14L, 5, psg, ol, null, null, "TIMED");

        Scorer scorer = new Scorer();
        scorer.setPlayerId(100L);
        scorer.setPlayerName("Dembele");
        scorer.setCompetitionCode(CODE);
        scorer.setTeam(psg);
        scorer.setPlayedMatches(3);
        scorer.setGoals(2);
        scorer.setPenalties(1);
        scorerRepository.save(scorer);
    }

    @Test
    void teamStats() {
        TeamStatsDto stats = teamStatsService.getTeamStats(CODE, psg.getId());

        assertEquals(3, stats.total().played());
        assertEquals(4, stats.total().points());
        assertEquals(1, stats.home().played());
        assertEquals(2, stats.away().played());
        assertEquals(1.0, stats.goalsForPerMatch());
        assertEquals(0.67, stats.goalsAgainstPerMatch());
        assertEquals(1, stats.cleanSheets());
        assertEquals(List.of("L", "D", "W"), stats.lastFiveResults());
    }

    @Test
    void finalStandings() {
        List<StandingRowDto> standings = standingsService.getStandings(CODE, null);

        assertEquals("OM", standings.get(0).teamName());
        assertEquals(6, standings.get(0).points());
        assertEquals("PSG", standings.get(1).teamName());
        assertEquals("OL", standings.get(2).teamName());
    }

    @Test
    void standingsAtMatchdayTwo() {
        List<StandingRowDto> standings = standingsService.getStandings(CODE, 2);

        assertEquals("PSG", standings.get(0).teamName());
        assertEquals("OL", standings.get(1).teamName());
        assertEquals("OM", standings.get(2).teamName());
    }

    @Test
    void rankingEvolution() {
        List<RankingPointDto> evolution = standingsService.getRankingEvolution(CODE, psg.getId());

        List<Integer> positions = evolution.stream().map(RankingPointDto::position).toList();
        assertEquals(List.of(1, 1, 1, 2), positions);
    }

    @Test
    void scorerStats() {
        ScorerStatsDto dembele = scorerStatsService.getScorerStats(CODE).get(0);

        assertEquals(1, dembele.nonPenaltyGoals());
        assertEquals(0.67, dembele.goalsPerMatch());
        assertEquals(3, dembele.teamGoals());
        assertEquals(66.67, dembele.shareOfTeamGoals());
    }

    private Team saveTeam(Long id, String name) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        return teamRepository.save(team);
    }

    private void saveMatch(Long id, int matchday, Team home, Team away, Integer homeScore, Integer awayScore, String status) {
        Match match = new Match();
        match.setId(id);
        match.setCompetitionCode(CODE);
        match.setMatchday(matchday);
        match.setUtcDate(OffsetDateTime.parse("2025-01-01T20:00:00Z").plusWeeks(matchday));
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setStatus(status);
        matchRepository.save(match);
    }
}
