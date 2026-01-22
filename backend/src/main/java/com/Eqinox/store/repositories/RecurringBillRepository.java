package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.RecurringBill;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecurringBillRepository extends JpaRepository<RecurringBill, Integer> {
    List<RecurringBill> findByUserId(Integer userId);

    @Query("""
                select b from RecurringBill b
                where b.active = true
                  and b.nextDueDate <= :today
            """)
    List<RecurringBill> findDueBills(@Param("today") LocalDate today);

    // ✅ locked fetch (for processor)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from RecurringBill b where b.id = :id")
    Optional<RecurringBill> findByIdForUpdate(@Param("id") Integer id);
    Optional<RecurringBill> findByIdAndUserId(Integer id, Integer userId);
}
