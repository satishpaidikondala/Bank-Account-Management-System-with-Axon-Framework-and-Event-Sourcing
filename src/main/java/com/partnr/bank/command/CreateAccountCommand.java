package com.partnr.bank.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class CreateAccountCommand {

    @TargetAggregateIdentifier
    private final String accountId;

    @NotBlank
    private final String ownerName;

    @PositiveOrZero
    private final double initialBalance;

    public CreateAccountCommand(String accountId, String ownerName, double initialBalance) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.initialBalance = initialBalance;
    }

    public String getAccountId() { return accountId; }
    public String getOwnerName() { return ownerName; }
    public double getInitialBalance() { return initialBalance; }
}
