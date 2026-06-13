package com.partnr.bank.aggregate;

import com.partnr.bank.command.CloseAccountCommand;
import com.partnr.bank.command.CreateAccountCommand;
import com.partnr.bank.command.DepositMoneyCommand;
import com.partnr.bank.command.WithdrawMoneyCommand;
import com.partnr.bank.event.AccountClosedEvent;
import com.partnr.bank.event.AccountCreatedEvent;
import com.partnr.bank.event.MoneyDepositedEvent;
import com.partnr.bank.event.MoneyWithdrawnEvent;
import com.partnr.bank.exception.AccountClosedException;
import com.partnr.bank.exception.InsufficientFundsException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Map;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate(snapshotTriggerDefinition = "bankAccountSnapshotTrigger")
public class BankAccount {

    @AggregateIdentifier
    private String accountId;
    private String ownerName;
    private double balance;
    private String status;

    protected BankAccount() {
    }

    @CommandHandler
    public BankAccount(CreateAccountCommand cmd) {
        apply(new AccountCreatedEvent(cmd.getAccountId(), cmd.getOwnerName(), cmd.getInitialBalance()));
    }

    @CommandHandler
    public Map<String, Object> handle(DepositMoneyCommand cmd) {
        if ("CLOSED".equals(status)) {
            throw new AccountClosedException(accountId);
        }
        double newBalance = balance + cmd.getAmount();
        apply(new MoneyDepositedEvent(accountId, cmd.getAmount(), newBalance));
        return Map.of(
            "message", "Deposit successful",
            "accountId", accountId,
            "amount", cmd.getAmount(),
            "balance", newBalance
        );
    }

    @CommandHandler
    public Map<String, Object> handle(WithdrawMoneyCommand cmd) {
        if ("CLOSED".equals(status)) {
            throw new AccountClosedException(accountId);
        }
        if (balance < cmd.getAmount()) {
            throw new InsufficientFundsException(
                "Insufficient funds. Available: " + balance + ", Requested: " + cmd.getAmount());
        }
        double newBalance = balance - cmd.getAmount();
        apply(new MoneyWithdrawnEvent(accountId, cmd.getAmount(), newBalance));
        return Map.of(
            "message", "Withdrawal successful",
            "accountId", accountId,
            "amount", cmd.getAmount(),
            "balance", newBalance
        );
    }

    @CommandHandler
    public void handle(CloseAccountCommand cmd) {
        if ("CLOSED".equals(status)) {
            throw new AccountClosedException(accountId);
        }
        if (balance != 0) {
            throw new IllegalStateException(
                "Cannot close account with non-zero balance: " + balance);
        }
        apply(new AccountClosedEvent(accountId));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.ownerName = event.getOwnerName();
        this.balance = event.getInitialBalance();
        this.status = "ACTIVE";
    }

    @EventSourcingHandler
    public void on(MoneyDepositedEvent event) {
        this.balance = event.getBalance();
    }

    @EventSourcingHandler
    public void on(MoneyWithdrawnEvent event) {
        this.balance = event.getBalance();
    }

    @EventSourcingHandler
    public void on(AccountClosedEvent event) {
        this.status = "CLOSED";
    }

    public String getAccountId() { return accountId; }
    public String getOwnerName() { return ownerName; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
}
