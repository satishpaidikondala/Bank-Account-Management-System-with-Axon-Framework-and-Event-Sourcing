package com.partnr.bank.event;

public class AccountCreatedEvent {

    private final String accountId;
    private final String ownerName;
    private final double initialBalance;

    public AccountCreatedEvent(String accountId, String ownerName, double initialBalance) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.initialBalance = initialBalance;
    }

    public String getAccountId() { return accountId; }
    public String getOwnerName() { return ownerName; }
    public double getInitialBalance() { return initialBalance; }
}
