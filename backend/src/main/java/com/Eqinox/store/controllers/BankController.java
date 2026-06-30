package com.Eqinox.store.controllers;

import com.Eqinox.store.api.ApiResponse;
import com.Eqinox.store.dtos.BankAccountDTO;
import com.Eqinox.store.dtos.BankTransactionDTO;
import com.Eqinox.store.entities.*;
import com.Eqinox.store.repositories.*;
import com.Eqinox.store.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;   // ✅ ADDED
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banking")
public class BankController {

    private final AuthUserService authUserService;
    private final BankAccountRepository bankRepo;
    private final UserBankLinkRepository userBankLinkRepo;
    private final BankSyncService bankSyncService;
    private final WalletService walletService;
    private final BankTransactionRepository txnRepo;

    public BankController(
            AuthUserService authUserService,
            BankAccountRepository bankRepo,
            UserBankLinkRepository userBankLinkRepo,
            BankSyncService bankSyncService,
            WalletService walletService,
            BankTransactionRepository txnRepo
    ) {
        this.authUserService = authUserService;
        this.bankRepo = bankRepo;
        this.userBankLinkRepo = userBankLinkRepo;
        this.bankSyncService = bankSyncService;
        this.walletService = walletService;
        this.txnRepo = txnRepo;
    }

    // ==========================================
    // CONNECT EXISTING BANK ACCOUNT
    // ==========================================
    @PostMapping("/connect")
    public ResponseEntity<ApiResponse<Void>> connectBank(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> body
    ) {

        Integer userId = authUserService.getUserId(auth);

        BankProvider provider =
                BankProvider.valueOf(body.get("provider"));

        String accountNumber = body.get("accountNumber");
        String pin = body.get("pin");

        BankAccount account = bankRepo
                .findByProviderAndAccountNumber(provider, accountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bank account not found"));

        if (!account.getPin().equals(pin)) {
            throw new IllegalArgumentException("Invalid PIN");
        }

        if (userBankLinkRepo.existsByUserIdAndBankAccount(userId, account)) {
            throw new IllegalStateException("Already linked");
        }

        UserBankLink link = new UserBankLink();
        link.setUserId(userId);
        link.setBankAccount(account);

        userBankLinkRepo.save(link);

        return ResponseEntity.ok(ApiResponse.ok());
    }

    // ==========================================
    // LIST USER LINKED BANKS
    // ==========================================
    @GetMapping
    public ResponseEntity<ApiResponse<List<BankAccountDTO>>> listBanks(
            @RequestHeader("Authorization") String auth
    ) {

        Integer userId = authUserService.getUserId(auth);

        List<BankAccountDTO> accounts =
                userBankLinkRepo.findByUserId(userId)
                        .stream()
                        .map(UserBankLink::getBankAccount)
                        .map(BankAccountDTO::new)
                        .toList();

        return ResponseEntity.ok(ApiResponse.ok(accounts));
    }

    // ==========================================
    // SYNC BANKS
    // ==========================================
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<Void>> syncBanks(
            @RequestHeader("Authorization") String auth
    ) {

        Integer userId = authUserService.getUserId(auth);

        bankSyncService.syncUserBanks(userId);

        return ResponseEntity.ok(ApiResponse.ok());
    }

    // ==========================================
    // TOP UP WALLET FROM BANK
    // ==========================================
    @PostMapping("/topup")
    public ResponseEntity<ApiResponse<String>> topUpFromBank(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> body
    ) {

        Integer userId = authUserService.getUserId(auth);

        Integer bankAccountId =
                Integer.valueOf(body.get("bankAccountId"));

        BigDecimal amount =
                new BigDecimal(body.get("amount"));

        BankAccount account = bankRepo.findById(bankAccountId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bank account not found"));

        boolean linked = userBankLinkRepo
                .existsByUserIdAndBankAccount(userId, account);

        if (!linked) {
            throw new IllegalStateException("Bank account not linked");
        }

        bankSyncService.processTopUpAsync(
                userId,
                bankAccountId,
                amount
        );

        return ResponseEntity.ok(
                ApiResponse.ok("Bank top-up is processing")
        );
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<BankTransactionDTO>>> listTransactions(
            @RequestHeader("Authorization") String auth
    ) {

        Integer userId = authUserService.getUserId(auth);

        List<UserBankLink> links =
                userBankLinkRepo.findByUserId(userId);

        List<BankAccount> accounts =
                links.stream()
                        .map(UserBankLink::getBankAccount)
                        .toList();

        List<BankTransactionDTO> txns =
                txnRepo.findByBankAccountIn(accounts)
                        .stream()
                        .map(BankTransactionDTO::new)
                        .toList();

        return ResponseEntity.ok(ApiResponse.ok(txns));
    }

    // ⚠️ ADMIN-ONLY: directly inflates a bank's mock balance
    @PreAuthorize("hasRole('ADMIN')")   // ✅ ADDED
    @PostMapping("/admin/fund")
    public ResponseEntity<ApiResponse<String>> fundBank(
            @RequestBody Map<String, String> body
    ) {

        Integer bankAccountId =
                Integer.valueOf(body.get("bankAccountId"));

        BigDecimal amount =
                new BigDecimal(body.get("amount"));

        BankAccount account = bankRepo.findById(bankAccountId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bank account not found"));

        BigDecimal newBalance =
                account.getMockBalance().add(amount);

        account.setMockBalance(newBalance);
        bankRepo.save(account);

        return ResponseEntity.ok(
                ApiResponse.ok("New balance: " + newBalance)
        );
    }

    // ⚠️ ADMIN-ONLY: external deposit into any account (mock/seed tool)
    @PreAuthorize("hasRole('ADMIN')")   // ✅ ADDED
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<String>> depositToBank(
            @RequestBody Map<String, String> body
    ) {

        BankProvider provider =
                BankProvider.valueOf(body.get("provider"));

        String accountNumber =
                body.get("accountNumber");

        BankAccount account = bankRepo
                .findByProviderAndAccountNumber(provider, accountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bank account not found"));

        BigDecimal amount =
                new BigDecimal(body.get("amount"));

        String description =
                body.getOrDefault("description", "External deposit");

        bankSyncService.depositToBank(
                account.getBankAccountId(),
                amount,
                description
        );

        return ResponseEntity.ok(
                ApiResponse.ok("Deposit successful")
        );
    }

    // ⚠️ ADMIN-ONLY: moves money between any two banks (mock/seed tool)
    @PreAuthorize("hasRole('ADMIN')")   // ✅ ADDED
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<String>> transferBetweenBanks(
            @RequestBody Map<String, String> body
    ) {

        BankProvider fromProvider =
                BankProvider.valueOf(body.get("fromProvider"));

        String fromAccount =
                body.get("fromAccountNumber");

        BankProvider toProvider =
                BankProvider.valueOf(body.get("toProvider"));

        String toAccount =
                body.get("toAccountNumber");

        BigDecimal amount =
                new BigDecimal(body.get("amount"));

        bankSyncService.transferBetweenBanks(
                fromProvider,
                fromAccount,
                toProvider,
                toAccount,
                amount
        );

        return ResponseEntity.ok(
                ApiResponse.ok("Transfer successful")
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> withdrawToBank(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> body
    ) {

        Integer userId = authUserService.getUserId(auth);

        BankProvider provider =
                BankProvider.valueOf(body.get("provider"));

        String accountNumber =
                body.get("accountNumber");

        BigDecimal amount =
                new BigDecimal(body.get("amount"));

        bankSyncService.withdrawToBank(
                userId,
                provider,
                accountNumber,
                amount
        );

        return ResponseEntity.ok(
                ApiResponse.ok("Withdrawal successful")
        );
    }

    // ⚠️ ADMIN-ONLY: seeds a mock bank account
    @PreAuthorize("hasRole('ADMIN')")   // ✅ ADDED
    @PostMapping("/mock/create")
    public ResponseEntity<ApiResponse<String>> createMockBank(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> body
    ) {

        BankProvider provider =
                BankProvider.valueOf(body.get("provider"));

        String accountNumber = body.get("accountNumber");

        if (bankRepo
            .findByProviderAndAccountNumber(provider, accountNumber)
            .isPresent()) {

            throw new IllegalStateException("Account already exists for this provider");
        }

        String ownerName = body.get("ownerName");

        BigDecimal initialBalance =
                new BigDecimal(body.getOrDefault("balance", "0"));

        BankAccount account = new BankAccount();
        account.setProvider(provider);
        account.setAccountNumber(accountNumber);
        account.setOwnerName(ownerName);
        account.setPin("1234");
        account.setMockBalance(initialBalance);

        bankRepo.save(account);

        return ResponseEntity.ok(
                ApiResponse.ok("Mock bank account created")
        );
    }
}