package com.foottracker.foottracker.repository;

import com.foottracker.foottracker.entity.Scorer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScorerRepository extends JpaRepository<Scorer, Long> {

    List<Scorer> findByCompetitionCodeOrderByGoalsDesc(String competitionCode);

    Optional<Scorer> findByPlayerIdAndCompetitionCode(Long playerId, String competitionCode);
}
