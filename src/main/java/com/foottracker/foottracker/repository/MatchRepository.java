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
}
