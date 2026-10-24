package com.foottracker.foottracker.service;

import com.foottracker.foottracker.dto.stats.FavoriteTeamDto;
import com.foottracker.foottracker.dto.stats.HeadToHeadDto;
import com.foottracker.foottracker.dto.stats.MatchdaySummaryDto;
import com.foottracker.foottracker.entity.Match;
import com.foottracker.foottracker.entity.Team;
import com.foottracker.foottracker.repository.FavoriteTeamRepository;
import com.foottracker.foottracker.repository.MatchRepository;
import com.foottracker.foottracker.repository.ScorerRepository;
import com.foottracker.foottracker.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:bonustest")
class BonusServicesTest {

    private static final String CODE = "FL1";

    @Autowired
    private HeadToHeadService headToHeadService;

    @Autowired
    private MatchdaySummaryService matchdaySummaryService;

    @Autowired
    private FavoriteTeamService favoriteTeamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ScorerRepository scorerRepository;

    @Autowired
    private FavoriteTeamRepository favoriteTeamRepository;

    private Team psg;
    private Team om;

    @BeforeEach
    void setUp() {
        favoriteTeamRepository.deleteAll();
        scorerRepository.deleteAll();
        matchRepository.deleteAll();
        teamRepository.deleteAll();

        psg = saveTeam(1L, "PSG");
        om = saveTeam(2L, "OM");
        Team ol = saveTeam(3L, "OL");
        Team losc = saveTeam(4L, "LOSC");

        saveMatch(10L, 1, psg, om, 2, 0, "FINISHED");
        saveMatch(11L, 1, ol, losc, 3, 3, "FINISHED");
        saveMatch(12L, 2, om, psg, 1, 0, "FINISHED");
        saveMatch(13L, 2, losc, ol, 0, 1, "FINISHED");
        saveMatch(14L, 3, psg, ol, null, null, "TIMED");
        saveMatch(15L, 3, om, losc, null, null, "TIMED");
    }

    @Test
    void headToHead() {
        HeadToHeadDto h2h = headToHeadService.getHeadToHead(CODE, psg.getId(), om.getId());

        assertEquals(2, h2h.matchesPlayed());
        assertEquals(1, h2h.team1Wins());
        assertEquals(1, h2h.team2Wins());
        assertEquals(0, h2h.draws());
        assertEquals(2, h2h.team1Goals());
        assertEquals(1, h2h.team2Goals());
        assertEquals(12L, h2h.matches().get(0).matchId());
    }

    @Test
    void headToHeadWithSameTeamIsRejected() {
        assertThrows(ResponseStatusException.class,
                () -> headToHeadService.getHeadToHead(CODE, psg.getId(), psg.getId()));
    }

    @Test
    void matchdaySummary() {
        MatchdaySummaryDto summary = matchdaySummaryService.getSummary(CODE, 1);

        assertEquals(2, summary.matchesPlayed());
        assertEquals(8, summary.totalGoals());
        assertEquals(4.0, summary.goalsPerMatch());
        assertEquals(1, summary.homeWins());
        assertEquals(1, summary.draws());
        assertEquals(10L, summary.biggestWin().matchId());
        assertEquals(11L, summary.highestScoringMatch().matchId());
    }

    @Test
    void matchdayNotPlayedYet() {
        MatchdaySummaryDto summary = matchdaySummaryService.getSummary(CODE, 3);

        assertEquals(0, summary.matchesPlayed());
        assertEquals(2, summary.matchesRemaining());
        assertNull(summary.biggestWin());
    }

    @Test
    void unknownMatchdayIsRejected() {
        assertThrows(ResponseStatusException.class, () -> matchdaySummaryService.getSummary(CODE, 9));
    }

    @Test
    void favorites() {
        favoriteTeamService.addFavorite(psg.getId());
        favoriteTeamService.addFavorite(psg.getId());

        List<FavoriteTeamDto> favorites = favoriteTeamService.getFavorites();
        assertEquals(1, favorites.size());

        FavoriteTeamDto favorite = favorites.get(0);
        assertEquals(List.of("L", "W"), favorite.lastFiveResults());
        assertEquals(12L, favorite.lastMatch().matchId());
        assertEquals(14L, favorite.nextMatch().matchId());

        favoriteTeamService.removeFavorite(psg.getId());
        assertTrue(favoriteTeamService.getFavorites().isEmpty());
        assertThrows(ResponseStatusException.class, () -> favoriteTeamService.removeFavorite(psg.getId()));
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
        match.setUtcDate(OffsetDateTime.parse("2025-01-01T20:00:00Z").plusWeeks(matchday).plusHours(id));
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setStatus(status);
        matchRepository.save(match);
    }
}
