package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.MonthlyAnalyticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonthlyAnalyticsSnapshotRepository extends JpaRepository<MonthlyAnalyticsSnapshot, Integer> {
    Optional<MonthlyAnalyticsSnapshot> findByUserIdAndMonthAndYear(Integer userId, Integer month, Integer year);
    boolean existsByUserIdAndMonthAndYear(
        Integer userId,
        Integer month,
        Integer year
    );
    List<MonthlyAnalyticsSnapshot>
    findByUserIdOrderByYearDescMonthDesc(Integer userId);
}
