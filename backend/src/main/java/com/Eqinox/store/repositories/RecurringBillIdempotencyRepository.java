package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.RecurringBillIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecurringBillIdempotencyRepository extends JpaRepository<RecurringBillIdempotency, Long> {

    Optional<RecurringBillIdempotency> findByUserIdAndIdemKeyAndOperation(
            Integer userId, String idemKey, String operation
    );

    boolean existsByUserIdAndIdemKeyAndOperation(Integer userId, String idemKey, String operation);
}
