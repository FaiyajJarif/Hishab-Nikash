package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.RecurringBillRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface RecurringBillRunRepository extends JpaRepository<RecurringBillRun, Long> {
    boolean existsByBillIdAndDueDate(Integer billId, LocalDate dueDate);
}
