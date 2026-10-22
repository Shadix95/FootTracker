package com.foottracker.foottracker.dto.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
class ApiDtoParsingTest {

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void parseMatches() {
        String json = """
                {
                  "filters": { "season": "2024" },
                  "competition": { "id": 2021, "name": "Premier League", "code": "PL", "type": "LEAGUE" },
                  "matches": [
                    {
                      "id": 497410,
                      "utcDate": "2024-08-16T19:00:00Z",
                      "status": "FINISHED",
                      "matchday": 1,
                      "homeTeam": { "id": 66, "name": "Manchester United FC", "shortName": "Man United", "tla": "MUN", "crest": "https://crests.football-data.org/66.png" },
                      "awayTeam": { "id": 63, "name": "Fulham FC", "shortName": "Fulham", "tla": "FUL", "crest": "https://crests.football-data.org/63.png" },
                      "score": { "winner": "HOME_TEAM", "duration": "REGULAR", "fullTime": { "home": 1, "away": 0 }, "halfTime": { "home": 0, "away": 0 } }
                    },
                    {
                      "id": 497999,
                      "utcDate": "2025-05-25T15:00:00Z",
                      "status": "TIMED",
                      "matchday": 38,
                      "homeTeam": { "id": 63, "name": "Fulham FC" },
                      "awayTeam": { "id": 66, "name": "Manchester United FC" },
                      "score": { "winner": null, "fullTime": { "home": null, "away": null } }
                    }
                  ]
                }
                """;

        MatchesResponse response = jsonMapper.readValue(json, MatchesResponse.class);

        assertEquals("PL", response.competition().code());
        assertEquals(2, response.matches().size());

        MatchDto first = response.matches().get(0);
        assertEquals("MUN", first.homeTeam().tla());
        assertEquals(1, first.score().fullTime().home());
        assertEquals("HOME_TEAM", first.score().winner());

        MatchDto notPlayed = response.matches().get(1);
        assertNull(notPlayed.score().fullTime().home());
    }

    @Test
    void parseScorers() {
        String json = """
                {
                  "count": 1,
                  "season": { "id": 2287, "startDate": "2024-08-16", "endDate": "2025-05-25", "currentMatchday": 38 },
                  "scorers": [
                    {
                      "player": { "id": 38101, "name": "Erling Haaland", "nationality": "Norway", "position": "Offence" },
                      "team": { "id": 65, "name": "Manchester City FC", "tla": "MCI" },
                      "playedMatches": 31,
                      "goals": 22,
                      "assists": null,
                      "penalties": 3
                    }
                  ]
                }
                """;

        ScorersResponse response = jsonMapper.readValue(json, ScorersResponse.class);

        ScorerDto haaland = response.scorers().get(0);
        assertEquals("Erling Haaland", haaland.player().name());
        assertEquals(22, haaland.goals());
        assertNull(haaland.assists());
        assertEquals(38, response.season().currentMatchday());
    }
}
