package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.TransferRequest;
import com.Eqinox.store.entities.BudgetPeriod;
import com.Eqinox.store.entities.Wallet;
import com.Eqinox.store.entities.WalletLedgerEntry;
import com.Eqinox.store.repositories.UserRepository;
import com.Eqinox.store.security.QrSecurityUtil;
import com.Eqinox.store.services.AuthUserService;
import com.Eqinox.store.services.BudgetService;
import com.Eqinox.store.services.WalletService;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

        private final WalletService walletService;
        private final AuthUserService authUserService;
        private final BudgetService budgetService;
        private final UserRepository userRepository;

        public WalletController(
                        WalletService walletService,
                        AuthUserService authUserService,
                        BudgetService budgetService,
                        UserRepository userRepository) {
                this.walletService = walletService;
                this.authUserService = authUserService;
                this.budgetService = budgetService;
                this.userRepository = userRepository;
        }

        // ✅ Get Wallet
        @GetMapping
        public ResponseEntity<ApiResponse<Wallet>> getWallet(
                        @RequestHeader("Authorization") String auth) {
                Integer userId = authUserService.getUserId(auth);
                return ResponseEntity.ok(
                                ApiResponse.ok(walletService.getWallet(userId)));
        }

        // 🔥 Transfer Money
        @PostMapping("/transfer")
        public ResponseEntity<ApiResponse<Void>> transfer(
                        @RequestHeader("Authorization") String auth,
                        @Valid @RequestBody TransferRequest request) {

                Integer fromUserId = authUserService.getUserId(auth);

                // 🔎 Find receiver by email
                var toUser = userRepository.findByEmail(request.getToEmail())
                                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

                Integer toUserId = toUser.getUserId();

                // 🚫 Prevent self transfer
                if (fromUserId.equals(toUserId)) {
                        throw new IllegalArgumentException("Cannot transfer to yourself");
                }

                walletService.transfer(
                                fromUserId,
                                toUserId,
                                request.getAmount(),
                                request.getIdempotencyKey());

                return ResponseEntity.ok(ApiResponse.ok());
        }

        @GetMapping("/history")
        public ResponseEntity<ApiResponse<List<WalletLedgerEntry>>> history(
                        @RequestHeader("Authorization") String auth,
                        @RequestParam(defaultValue = "20") int limit) {

                Integer userId = authUserService.getUserId(auth);

                return ResponseEntity.ok(
                                ApiResponse.ok(walletService.getHistory(userId, limit)));
        }

        @PostMapping("/move-to-budget")
        public ResponseEntity<ApiResponse<Void>> moveToBudget(
                        @RequestHeader("Authorization") String auth,
                        @RequestParam BigDecimal amount) {

                Integer userId = authUserService.getUserId(auth);

                // 1️⃣ Deduct from wallet
                walletService.deduct(userId, amount, "Moved to budget");

                // 2️⃣ Add to current month's budget
                BudgetPeriod period = budgetService.getOrCreateCurrentBudget(userId);
                budgetService.addIncome(period, amount);

                return ResponseEntity.ok(ApiResponse.ok());
        }

        @PostMapping("/topup")
        public ResponseEntity<ApiResponse<Void>> topUp(
                        @RequestHeader("Authorization") String auth,
                        @RequestBody Map<String, BigDecimal> body) {

                Integer userId = authUserService.getUserId(auth);

                BigDecimal amount = body.get("amount");

                walletService.topUp(userId, amount, "Manual top-up");

                return ResponseEntity.ok(ApiResponse.ok());
        }

        @PostMapping("/transfer/qr")
        public ResponseEntity<ApiResponse<String>> transferViaQr(
                        @RequestHeader("Authorization") String auth,
                        @RequestBody Map<String, String> body) {

                Integer senderId = authUserService.getUserId(auth);

                String receiverEmail = body.get("email");
                BigDecimal amount = new BigDecimal(body.get("amount"));

                walletService.transferByEmail(
                                senderId,
                                receiverEmail,
                                amount);

                return ResponseEntity.ok(
                                ApiResponse.ok("QR transfer successful"));
        }

        @PostMapping("/qr/generate")
        public ResponseEntity<ApiResponse<String>> generateQr(
                        @RequestHeader("Authorization") String auth,
                        @RequestBody Map<String, String> body) throws Exception {

                Integer userId = authUserService.getUserId(auth);

                Wallet wallet = walletService.getWallet(userId);

                String email = userRepository.findById(userId)
                                .orElseThrow()
                                .getEmail();

                String amount = body.get("amount");

                long exp = System.currentTimeMillis() + (5 * 60 * 1000); // 5 min expiry

                String nonce = java.util.UUID.randomUUID().toString();

                String payload = String.format(
                                "%s|%s|%d|%s",
                                email,
                                amount,
                                exp,
                                nonce);

                String signature = QrSecurityUtil.sign(payload);

                String qrContent = payload + "::" + signature;

                String base64Qr = walletService.generateQrImageBase64(qrContent);

                return ResponseEntity.ok(ApiResponse.ok(base64Qr));
        }

        @PostMapping("/qr/pay")
        public ResponseEntity<ApiResponse<String>> payViaQr(
                @RequestHeader("Authorization") String auth,
                @RequestBody Map<String, String> body) {
        
            Integer senderId = authUserService.getUserId(auth);
        
            String qrContent = body.get("qr");
            if (qrContent == null || qrContent.isBlank())
                throw new IllegalArgumentException("QR content missing");
            qrContent = qrContent.trim().replaceAll("\\s+", "");

            String[] parts = qrContent.split("::");
            if (parts.length != 2)
                throw new IllegalArgumentException("Invalid QR");
        
            String payload = parts[0];
            String signature = parts[1];
        
            if (!QrSecurityUtil.verify(payload, signature))
                throw new IllegalStateException("QR tampered");
        
            String[] fields = payload.split("\\|");
            if (fields.length < 3)
                throw new IllegalArgumentException("Invalid QR format");
        
            String email = fields[0];
            BigDecimal amount = new BigDecimal(fields[1]);
            long exp = Long.parseLong(fields[2]);
        
            if (System.currentTimeMillis() > exp)
                throw new IllegalStateException("QR expired");
        
            walletService.transferByEmail(senderId, email, amount);
        
            return ResponseEntity.ok(ApiResponse.ok("QR payment successful"));
        }        

}
