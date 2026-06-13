package com.partnr.bank.projection;

import com.partnr.bank.entity.CurrentAccountView;
import com.partnr.bank.event.AccountClosedEvent;
import com.partnr.bank.event.AccountCreatedEvent;
import com.partnr.bank.event.MoneyDepositedEvent;
import com.partnr.bank.event.MoneyWithdrawnEvent;
import com.partnr.bank.repository.CurrentAccountViewRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.config.ProcessingGroup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ProcessingGroup("current_account_view")
public class CurrentAccountViewProjection {

    private final CurrentAccountViewRepository repository;

    public CurrentAccountViewProjection(CurrentAccountViewRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    @Transactional
    public void on(AccountCreatedEvent event) {
        repository.save(new CurrentAccountView(
            event.getAccountId(),
            event.getOwnerName(),
            event.getInitialBalance(),
            "ACTIVE"
        ));
    }

    @EventHandler
    @Transactional
    public void on(MoneyDepositedEvent event) {
        repository.findById(event.getAccountId()).ifPresent(view -> {
            view.setBalance(event.getBalance());
            repository.save(view);
        });
    }

    @EventHandler
    @Transactional
    public void on(MoneyWithdrawnEvent event) {
        repository.findById(event.getAccountId()).ifPresent(view -> {
            view.setBalance(event.getBalance());
            repository.save(view);
        });
    }

    @EventHandler
    @Transactional
    public void on(AccountClosedEvent event) {
        repository.findById(event.getAccountId()).ifPresent(view -> {
            view.setStatus("CLOSED");
            repository.save(view);
        });
    }
}
