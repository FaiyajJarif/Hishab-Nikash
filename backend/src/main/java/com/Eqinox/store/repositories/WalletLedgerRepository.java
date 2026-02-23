package com.Eqinox.store.repositories;

import com.Eqinox.store.entities.LedgerType;
import com.Eqinox.store.entities.TransferStatus;
import com.Eqinox.store.entities.WalletLedgerEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface WalletLedgerRepository
        extends JpaRepository<WalletLedgerEntry, Integer> {


    boolean existsByReferenceIdStartingWith(String referenceId);
    Optional<WalletLedgerEntry> findByReferenceId(String referenceId);
    List<WalletLedgerEntry> findByWalletIdOrderByCreatedAtDesc(
            Integer walletId,
            Pageable pageable
    );
    @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM WalletLedgerEntry e
    WHERE e.walletId = :walletId
      AND e.type = com.Eqinox.store.entities.LedgerType.TRANSFER_OUT
      AND e.status = com.Eqinox.store.entities.TransferStatus.SUCCESS
      AND e.createdAt >= :start
      AND e.createdAt < :end
        """)
        BigDecimal sumTodayTransfers(
                Integer walletId,
                OffsetDateTime start,
                OffsetDateTime end
        );
        @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM WalletLedgerEntry e
    WHERE e.walletId = :walletId
      AND e.status = :status
      AND e.type IN :types
      AND e.createdAt >= :start
      AND e.createdAt < :end
""")
BigDecimal sumMonthlyWalletIncome(
        Integer walletId,
        TransferStatus status,
        List<LedgerType> types,
        OffsetDateTime start,
        OffsetDateTime end
);


}
