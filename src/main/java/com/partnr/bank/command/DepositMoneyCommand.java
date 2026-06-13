package com.partnr.bank.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import jakarta.validation.constraints.Positive;

public class DepositMoneyCommand {

    @TargetAggregateIdentifier
    private final String accountId;

    @Positive
    private final double amount;

    public DepositMoneyCommand(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public String getAccountId() { return accountId; }
    public double getAmount() { return amount; }
}
