package com.partnr.bank.event;

public class MoneyWithdrawnEvent {

    private final String accountId;
    private final double amount;
    private final double balance;

    public MoneyWithdrawnEvent(String accountId, double amount, double balance) {
        this.accountId = accountId;
        this.amount = amount;
        this.balance = balance;
    }

    public String getAccountId() { return accountId; }
    public double getAmount() { return amount; }
    public double getBalance() { return balance; }
}
