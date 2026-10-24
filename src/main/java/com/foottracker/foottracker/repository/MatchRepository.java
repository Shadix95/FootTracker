package com.foottracker.foottracker.repository;

import com.foottracker.foottracker.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByCompetitionCodeAndStatusOrderByUtcDateAsc(String competitionCode, String status);

    List<Match> findByCompetitionCodeAndMatchdayOrderByUtcDateAsc(String competitionCode, Integer matchday);

    @Query("""
            SELECT m FROM Match m
            WHERE m.competitionCode = :code
              AND m.status = 'FINISHED'
              AND (m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId)
            ORDER BY m.utcDate ASC
            """)
    List<Match> findFinishedMatchesOfTeam(@Param("code") String competitionCode, @Param("teamId") Long teamId);

    @Query("""
            SELECT m FROM Match m
            WHERE m.competitionCode = :code
              AND m.status = 'FINISHED'
              AND ((m.homeTeam.id = :team1 AND m.awayTeam.id = :team2)
                OR (m.homeTeam.id = :team2 AND m.awayTeam.id = :team1))
            ORDER BY m.utcDate DESC
            """)
    List<Match> findHeadToHead(@Param("code") String competitionCode,
                               @Param("team1") Long team1Id,
                               @Param("team2") Long team2Id);

    @Query("""
            SELECT m FROM Match m
            WHERE m.status = 'FINISHED'
              AND (m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId)
            ORDER BY m.utcDate DESC
            """)
    List<Match> findLastFinishedMatches(@Param("teamId") Long teamId);

    @Query("""
            SELECT m FROM Match m
            WHERE m.status IN ('SCHEDULED', 'TIMED')
              AND (m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId)
            ORDER BY m.utcDate ASC
            """)
    List<Match> findUpcomingMatches(@Param("teamId") Long teamId);
}
