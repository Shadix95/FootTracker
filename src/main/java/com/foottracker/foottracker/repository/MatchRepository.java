package com.foottracker.foottracker.repository;

import com.foottracker.foottracker.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByCompetitionCodeAndStatusOrderByUtcDateAsc(String competitionCode, String status);

    List<Match> findByCompetitionCodeAndMatchdayOrderByUtcDateAsc(String competitionCode, Integer matchday);
}
