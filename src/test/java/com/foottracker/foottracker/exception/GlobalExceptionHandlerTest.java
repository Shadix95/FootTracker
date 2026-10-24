package com.foottracker.foottracker.exception;

import com.foottracker.foottracker.client.FootballDataClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:errortest")
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FootballDataClient footballDataClient;

    @Test
    void invalidApiKeyReturns403() throws Exception {
        when(footballDataClient.getStandings("PL")).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        mockMvc.perform(get("/api/raw/competitions/PL/standings"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.path").value("/api/raw/competitions/PL/standings"));
    }

    @Test
    void quotaExceededReturns429() throws Exception {
        when(footballDataClient.getScorers("PL")).thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        mockMvc.perform(get("/api/raw/competitions/PL/scorers"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429));
    }

    @Test
    void unknownTeamReturns404() throws Exception {
        mockMvc.perform(get("/api/competitions/PL/teams/999999/stats"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Equipe introuvable : 999999"));
    }

    @Test
    void invalidTeamIdReturns400() throws Exception {
        mockMvc.perform(get("/api/competitions/PL/teams/abc/stats"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownUrlReturns404() throws Exception {
        mockMvc.perform(get("/cette/url/nexiste/pas"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getOnSyncReturns405() throws Exception {
        mockMvc.perform(get("/api/sync/PL"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void swaggerDocumentationIsAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("FootTracker API"));
    }
}
