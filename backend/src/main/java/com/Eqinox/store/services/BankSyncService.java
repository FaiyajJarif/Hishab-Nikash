package com.Eqinox.store.services;

import com.Eqinox.store.entities.BankAccount;
import com.Eqinox.store.entities.BankProvider;
import com.Eqinox.store.entities.BankTransaction;
import com.Eqinox.store.entities.UserBankLink;
import com.Eqinox.store.repositories.BankAccountRepository;
import com.Eqinox.store.repositories.BankTransactionRepository;
import com.Eqinox.store.repositories.UserBankLinkRepository;
import com.Eqinox.store.services.bank.BkashConnector;
import com.Eqinox.store.services.bank.DbblConnector;
import com.Eqinox.store.services.bank.NagadConnector;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class BankSyncService {

    private final UserBankLinkRepository linkRepo;
    private final BankAccountRepository bankRepo;
    private final WalletService walletService;
    private final DbblConnector dbbl;
    private final BkashConnector bkash;
    private final NagadConnector nagad;
    private final BankTransactionRepository txnRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public BankSyncService(
            UserBankLinkRepository linkRepo,
            BankAccountRepository bankRepo,
            WalletService walletService,
            DbblConnector dbbl,
            BkashConnector bkash,
            NagadConnector nagad,
            BankTransactionRepository txnRepo,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.linkRepo = linkRepo;
        this.bankRepo = bankRepo;
        this.walletService = walletService;
        this.dbbl = dbbl;
        this.bkash = bkash;
        this.nagad = nagad;
        this.txnRepo = txnRepo;
        this.messagingTemplate = messagingTemplate;
    }

    // ==========================================
    // SYNC LINKED BANK ACCOUNTS
    // ==========================================
    public void syncUserBanks(Integer userId) {

        List<UserBankLink> links = linkRepo.findByUserId(userId);

        for (UserBankLink link : links) {

            BankAccount acc = link.getBankAccount();

            BigDecimal balance = switch (acc.getProvider()) {
                case DBBL -> dbbl.fetchBalance(acc);
                case BKASH -> bkash.fetchBalance(acc);
                case NAGAD -> nagad.fetchBalance(acc);
            };

            if (balance.compareTo(BigDecimal.ZERO) > 0) {

                walletService.topUp(
                        userId,
                        balance,
                        acc.getProvider() + " bank sync"
                );

                acc.setMockBalance(BigDecimal.ZERO);
                bankRepo.save(acc);
            }
        }
    }

    // ==========================================
    // TOP UP FROM SPECIFIC BANK ACCOUNT
    // ==========================================
    @Async
    @Transactional
    public void processTopUpAsync(
            Integer userId,
            Integer bankAccountId,
            BigDecimal amount
    ) {

        System.out.println("=== ASYNC METHOD STARTED ===");

        BankAccount account = bankRepo.findById(bankAccountId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bank account not found"));

        // Create transaction as PENDING
        BankTransaction txn = new BankTransaction();
        txn.setBankAccount(account);
        txn.setType("DEBIT");
        txn.setStatus("PENDING");
        txn.setAmount(amount);
        txn.setDescription("Wallet top-up processing");

        // IMPORTANT FIX: set current balance
        txn.setBalanceAfter(account.getMockBalance());

        txnRepo.save(txn);

        try {

            Thread.sleep(3000);

            if (account.getMockBalance().compareTo(amount) < 0) {

                txn.setStatus("FAILED");
                txn.setDescription("Insufficient funds");
                txnRepo.save(txn);
                return;
            }

            BigDecimal newBalance =
                    account.getMockBalance().subtract(amount);

            account.setMockBalance(newBalance);
            bankRepo.save(account);

            txn.setStatus("SUCCESS");
            txn.setBalanceAfter(newBalance);
            txn.setDescription("Wallet top-up success");

            txnRepo.save(txn);

            walletService.topUp(
                    userId,
                    amount,
                    "Bank top-up from " + account.getProvider().name()
            );

            messagingTemplate.convertAndSend(
                "/topic/bank-updates/" + userId,
                "Top-up SUCCESS: " + amount
            );        

            System.out.println("=== TOPUP SUCCESS ===");

        } catch (InterruptedException e) {

            txn.setStatus("FAILED");
            txn.setDescription("Processing interrupted");
            messagingTemplate.convertAndSend(
                "/topic/bank-updates/" + userId,
                "Top-up FAILED"
            );        
            txnRepo.save(txn);
        }
    }

    // ==========================================
    // DEDUCT FROM FIRST LINKED BANK
    // ==========================================
    public void deductFromDefaultBank(
            Integer userId,
            BigDecimal amount
    ) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        List<UserBankLink> links = linkRepo.findByUserId(userId);

        if (links.isEmpty()) {
            throw new IllegalStateException("No bank account connected");
        }

        BankAccount account = links.get(0).getBankAccount();

        if (account.getMockBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient bank balance");
        }

        account.setMockBalance(
                account.getMockBalance().subtract(amount)
        );

        bankRepo.save(account);
    }

    @Transactional
    public void depositToBank(
            Integer bankAccountId,
            BigDecimal amount,
            String description
    ) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        BankAccount account = bankRepo.findById(bankAccountId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bank account not found"));

        BigDecimal newBalance =
                account.getMockBalance().add(amount);

        account.setMockBalance(newBalance);
        bankRepo.save(account);

        BankTransaction txn = new BankTransaction();
        txn.setBankAccount(account);
        txn.setAmount(amount);
        txn.setType("CREDIT");
        txn.setStatus("SUCCESS");
        txn.setBalanceAfter(newBalance);
        txn.setDescription(description);

        txnRepo.save(txn);
    }

    @Transactional
    public void transferBetweenBanks(
            BankProvider fromProvider,
            String fromAccountNumber,
            BankProvider toProvider,
            String toAccountNumber,
            BigDecimal amount
    ) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        BankAccount sender = bankRepo
                .findByProviderAndAccountNumber(fromProvider, fromAccountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Sender account not found"));

        BankAccount receiver = bankRepo
                .findByProviderAndAccountNumber(toProvider, toAccountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Receiver account not found"));

        if (sender.getMockBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        // Deduct sender
        BigDecimal senderNewBalance =
                sender.getMockBalance().subtract(amount);

        sender.setMockBalance(senderNewBalance);
        bankRepo.save(sender);

        BankTransaction senderTxn = new BankTransaction();
        senderTxn.setBankAccount(sender);
        senderTxn.setAmount(amount);
        senderTxn.setType("DEBIT");
        senderTxn.setStatus("SUCCESS");
        senderTxn.setBalanceAfter(senderNewBalance);
        senderTxn.setDescription("Transfer to " + toAccountNumber);

        txnRepo.save(senderTxn);

        // Credit receiver
        BigDecimal receiverNewBalance =
                receiver.getMockBalance().add(amount);

        receiver.setMockBalance(receiverNewBalance);
        bankRepo.save(receiver);

        BankTransaction receiverTxn = new BankTransaction();
        receiverTxn.setBankAccount(receiver);
        receiverTxn.setAmount(amount);
        receiverTxn.setType("CREDIT");
        receiverTxn.setStatus("SUCCESS");
        receiverTxn.setBalanceAfter(receiverNewBalance);
        receiverTxn.setDescription("Transfer from " + fromAccountNumber);

        txnRepo.save(receiverTxn);

        // Notify both users if linked
        notifyLinkedUsers(sender, "Money sent: " + amount);
        notifyLinkedUsers(receiver, "Money received: " + amount);
    }

    private void notifyLinkedUsers(BankAccount account, String message) {

        List<UserBankLink> links =
                linkRepo.findAll()
                        .stream()
                        .filter(l -> l.getBankAccount().equals(account))
                        .toList();
    
        for (UserBankLink link : links) {
    
            messagingTemplate.convertAndSend(
                    "/topic/bank-updates/" + link.getUserId(),
                    message
            );
        }
    }    
    @Transactional
    public void withdrawToBank(
            Integer userId,
            BankProvider provider,
            String accountNumber,
            BigDecimal amount
    ) {
    
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    
        // Get linked bank account
        BankAccount account = bankRepo
                .findByProviderAndAccountNumber(provider, accountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bank account not found"));
    
        boolean linked = linkRepo
                .existsByUserIdAndBankAccount(userId, account);
    
        if (!linked) {
            throw new IllegalStateException("Bank account not linked");
        }
    
        // Deduct from wallet
        walletService.deduct(
                userId,
                amount,
                "Withdrawal to " + provider.name()
        );
    
        // Credit bank
        BigDecimal newBalance =
                account.getMockBalance().add(amount);
    
        account.setMockBalance(newBalance);
        bankRepo.save(account);
    
        // Create bank transaction
        BankTransaction txn = new BankTransaction();
        txn.setBankAccount(account);
        txn.setAmount(amount);
        txn.setType("CREDIT");
        txn.setStatus("SUCCESS");
        txn.setBalanceAfter(newBalance);
        txn.setDescription("Wallet withdrawal");
    
        txnRepo.save(txn);
    
        messagingTemplate.convertAndSend(
                "/topic/bank-updates/" + userId,
                "Withdrawal SUCCESS: " + amount
        );
    }
    
}
