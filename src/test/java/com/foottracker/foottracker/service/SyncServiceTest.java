package com.foottracker.foottracker.service;

import com.foottracker.foottracker.client.FootballDataClient;
import com.foottracker.foottracker.dto.api.MatchDto;
import com.foottracker.foottracker.dto.api.MatchesResponse;
import com.foottracker.foottracker.dto.api.PlayerDto;
import com.foottracker.foottracker.dto.api.ScoreDetailDto;
import com.foottracker.foottracker.dto.api.ScoreDto;
import com.foottracker.foottracker.dto.api.ScorerDto;
import com.foottracker.foottracker.dto.api.ScorersResponse;
import com.foottracker.foottracker.dto.api.TeamDto;
import com.foottracker.foottracker.entity.Match;
import com.foottracker.foottracker.repository.MatchRepository;
import com.foottracker.foottracker.repository.ScorerRepository;
import com.foottracker.foottracker.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:synctest")
class SyncServiceTest {

    @Autowired
    private SyncService syncService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ScorerRepository scorerRepository;

    @MockitoBean
    private FootballDataClient footballDataClient;

    @Test
    void syncCompetitionSavesDataWithoutDuplicates() {
        TeamDto psg = new TeamDto(524L, "Paris Saint-Germain FC", "PSG", "PSG", null);
        TeamDto om = new TeamDto(516L, "Olympique de Marseille", "Marseille", "MAR", null);

        MatchDto played = new MatchDto(1L, OffsetDateTime.parse("2024-10-27T19:45:00Z"), "FINISHED", 9,
                om, psg, new ScoreDto("AWAY_TEAM", new ScoreDetailDto(0, 3)));
        MatchDto notPlayed = new MatchDto(2L, OffsetDateTime.parse("2025-03-16T19:45:00Z"), "TIMED", 26,
                psg, om, new ScoreDto(null, new ScoreDetailDto(null, null)));

        ScorerDto scorer = new ScorerDto(new PlayerDto(10L, "Ousmane Dembele", "France"), psg, 29, 21, 6, 0);

        when(footballDataClient.getMatches("FL1")).thenReturn(new MatchesResponse(null, List.of(played, notPlayed)));
        when(footballDataClient.getScorers("FL1")).thenReturn(new ScorersResponse(null, null, List.of(scorer)));

        syncService.syncCompetition("FL1");
        syncService.syncCompetition("FL1");

        assertEquals(2, teamRepository.count());
        assertEquals(2, matchRepository.count());
        assertEquals(1, scorerRepository.count());

        Match savedMatch = matchRepository.findById(1L).orElseThrow();
        assertEquals(3, savedMatch.getAwayScore());
        assertEquals("PSG", savedMatch.getAwayTeam().getTla());
    }
}
