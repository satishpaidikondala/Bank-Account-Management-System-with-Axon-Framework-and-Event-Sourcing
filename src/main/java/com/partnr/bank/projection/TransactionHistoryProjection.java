package com.partnr.bank.projection;

import com.partnr.bank.entity.TransactionHistoryEntry;
import com.partnr.bank.event.AccountCreatedEvent;
import com.partnr.bank.event.MoneyDepositedEvent;
import com.partnr.bank.event.MoneyWithdrawnEvent;
import com.partnr.bank.repository.TransactionHistoryEntryRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.config.ProcessingGroup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@ProcessingGroup("transaction_history")
public class TransactionHistoryProjection {

    private final TransactionHistoryEntryRepository repository;

    public TransactionHistoryProjection(TransactionHistoryEntryRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    @Transactional
    public void on(AccountCreatedEvent event) {
        repository.save(new TransactionHistoryEntry(
            event.getAccountId(),
            "ACCOUNT_CREATED",
            event.getInitialBalance(),
            event.getInitialBalance(),
            Instant.now()
        ));
    }

    @EventHandler
    @Transactional
    public void on(MoneyDepositedEvent event) {
        repository.save(new TransactionHistoryEntry(
            event.getAccountId(),
            "DEPOSIT",
            event.getAmount(),
            event.getBalance(),
            Instant.now()
        ));
    }

    @EventHandler
    @Transactional
    public void on(MoneyWithdrawnEvent event) {
        repository.save(new TransactionHistoryEntry(
            event.getAccountId(),
            "WITHDRAWAL",
            event.getAmount(),
            event.getBalance(),
            Instant.now()
        ));
    }
}
