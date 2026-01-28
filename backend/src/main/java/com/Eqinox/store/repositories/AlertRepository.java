package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
    List<Alert> findTop10ByUserIdOrderByCreatedAtDesc(Integer userId);
    boolean existsByUserIdAndMessageAndCreatedAtAfter(
    Integer userId,
    String message,
    LocalDateTime after
);

}
