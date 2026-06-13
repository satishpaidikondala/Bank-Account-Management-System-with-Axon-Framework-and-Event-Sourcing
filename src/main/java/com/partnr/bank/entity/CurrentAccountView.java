package com.partnr.bank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "current_account_view")
public class CurrentAccountView {

    @Id
    private String accountId;
    private String ownerName;
    private double balance;
    private String status;

    public CurrentAccountView() {
    }

    public CurrentAccountView(String accountId, String ownerName, double balance, String status) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.balance = balance;
        this.status = status;
    }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
