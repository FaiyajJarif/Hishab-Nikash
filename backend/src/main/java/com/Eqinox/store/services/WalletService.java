package com.Eqinox.store.services;

import com.Eqinox.store.entities.*;
import com.Eqinox.store.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepo;
    private final WalletLedgerRepository ledgerRepo;
    private final UserWsNotificationService wsService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    public WalletService(
            WalletRepository walletRepo,
            WalletLedgerRepository ledgerRepo,
            UserWsNotificationService wsService,
            SimpMessagingTemplate messagingTemplate,
            UserRepository userRepository
    ) {
        this.walletRepo = walletRepo;
        this.ledgerRepo = ledgerRepo;
        this.wsService = wsService;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    public Wallet getOrCreateWallet(Integer userId) {
        return walletRepo.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUserId(userId);
                    w.setBalance(BigDecimal.ZERO);
                    return walletRepo.save(w);
                });
    }

    public Wallet getWallet(Integer userId) {
        return getOrCreateWallet(userId);
    }

    public void topUp(Integer userId, BigDecimal amount, String description) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        // ✅ lock wallet row
        Wallet wallet = walletRepo.findByUserIdForUpdate(userId)
                .orElseGet(() -> getOrCreateWallet(userId)); // if not exist create

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(OffsetDateTime.now());
        walletRepo.save(wallet);

        messagingTemplate.convertAndSend(
            "/topic/wallet/" + userId,
            wallet.getBalance()
        );    
        createLedger(wallet, LedgerType.TOPUP, amount, description, null);

        wsService.broadcastToUser(
            userId,
            Map.of(
                "type", "WALLET_UPDATED",
                "amount", amount
            )
        );
        }

    public void deduct(Integer userId, BigDecimal amount, String description) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        // ✅ lock wallet row
        Wallet wallet = walletRepo.findByUserIdForUpdate(userId)
                .orElseGet(() -> getOrCreateWallet(userId));

        if (wallet.getBalance().compareTo(amount) < 0)
            throw new IllegalStateException("Insufficient wallet balance");

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(OffsetDateTime.now());
        walletRepo.save(wallet);

        createLedger(wallet, LedgerType.WITHDRAW, amount.negate(), description, null);

        wsService.broadcastToUser(
            userId,
            Map.of(
                "type", "WALLET_UPDATED",
                "amount", amount.negate()
            )
        );
        }

    public void transfer(Integer fromUserId,
        Integer toUserId,
        BigDecimal amount,
        String idempotencyKey) {

if (amount.compareTo(BigDecimal.ZERO) <= 0)
throw new IllegalArgumentException("Amount must be positive");

if (idempotencyKey == null || idempotencyKey.isBlank())
throw new IllegalArgumentException("idempotencyKey is required");

// ✅ Proper idempotency check (prefix based)
if (ledgerRepo.existsByReferenceIdStartingWith(idempotencyKey)) {
return; // already processed
}

Wallet fromBase = getOrCreateWallet(fromUserId);
Wallet toBase = getOrCreateWallet(toUserId);

Integer fromWalletId = fromBase.getWalletId();
Integer toWalletId = toBase.getWalletId();

// Deadlock-safe locking
Integer firstLock = Math.min(fromWalletId, toWalletId);
Integer secondLock = Math.max(fromWalletId, toWalletId);

Wallet first = walletRepo.findByWalletIdForUpdate(firstLock)
.orElseThrow();
Wallet second = walletRepo.findByWalletIdForUpdate(secondLock)
.orElseThrow();

Wallet from = first.getWalletId().equals(fromWalletId) ? first : second;
Wallet to = from == first ? second : first;

if (from.getBalance().compareTo(amount) < 0)
throw new IllegalStateException("Insufficient wallet balance");

// Daily limit
OffsetDateTime now = OffsetDateTime.now();
OffsetDateTime start = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());
OffsetDateTime end = start.plusDays(1);

BigDecimal todayTransferred =
ledgerRepo.sumTodayTransfers(from.getWalletId(), start, end);

if (todayTransferred.add(amount)
.compareTo(from.getDailyLimit()) > 0) {
throw new IllegalStateException("Daily wallet limit exceeded");
}

// Fraud simulation
if (amount.compareTo(new BigDecimal("50000")) > 0) {
    wsService.broadcastToUser(
        fromUserId,
        Map.of(
            "type", "FRAUD_ALERT",
            "message", "Suspicious transfer detected",
            "amount", amount
        )
    );
    }

// Apply balances
from.setBalance(from.getBalance().subtract(amount));
to.setBalance(to.getBalance().add(amount));

from.setUpdatedAt(now);
to.setUpdatedAt(now);

walletRepo.save(from);
walletRepo.save(to);

// Create ledger entries AFTER balance update
createLedger(from,
LedgerType.TRANSFER_OUT,
amount.negate(),
"Transfer to user " + toUserId,
idempotencyKey + "-OUT");

createLedger(to,
LedgerType.TRANSFER_IN,
amount,
"Transfer from user " + fromUserId,
idempotencyKey + "-IN");

wsService.broadcastToUser(
    fromUserId,
    Map.of(
        "type", "WALLET_UPDATED"
    )
);

wsService.broadcastToUser(
    toUserId,
    Map.of(
        "type", "WALLET_UPDATED"
    )
);
}

    private WalletLedgerEntry createLedger(
            Wallet wallet,
            LedgerType type,
            BigDecimal amount,
            String description,
            String referenceId
    ) {
        WalletLedgerEntry entry = new WalletLedgerEntry();
        entry.setWalletId(wallet.getWalletId());
        entry.setType(type);
        entry.setAmount(amount);
        entry.setDescription(description);

        if (referenceId == null || referenceId.isBlank()) {
            referenceId = "SYS-" + System.currentTimeMillis();
        }
        entry.setReferenceId(referenceId); // ✅ IMPORTANT for idempotency
        return ledgerRepo.save(entry);
    }

    public List<WalletLedgerEntry> getHistory(Integer userId, int limit) {
        Wallet wallet = getOrCreateWallet(userId);

        int safeLimit = Math.max(1, Math.min(limit, 100));

        return ledgerRepo.findByWalletIdOrderByCreatedAtDesc(
                wallet.getWalletId(),
                PageRequest.of(0, safeLimit)
        );
    }

    @Transactional
    public void transferByEmail(
            Integer senderId,
            String receiverEmail,
            BigDecimal amount
    ) {
    
        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Receiver not found"));
    
        // Reuse main transfer method
        transfer(
                senderId,
                receiver.getUserId(),
                amount,
                "QR-" + System.currentTimeMillis()
        );
    }    
    public String generateQrImageBase64(String text) throws Exception {

        var bitMatrix =
                new com.google.zxing.qrcode.QRCodeWriter()
                        .encode(text,
                                com.google.zxing.BarcodeFormat.QR_CODE,
                                300,
                                300);
    
        java.io.ByteArrayOutputStream pngOutputStream =
                new java.io.ByteArrayOutputStream();
    
        com.google.zxing.client.j2se.MatrixToImageWriter
                .writeToStream(bitMatrix, "PNG", pngOutputStream);
    
        byte[] pngData = pngOutputStream.toByteArray();
    
        return java.util.Base64.getEncoder()
                .encodeToString(pngData);
    }
    
}
