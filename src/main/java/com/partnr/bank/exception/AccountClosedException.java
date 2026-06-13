package com.partnr.bank.exception;

public class AccountClosedException extends RuntimeException {
    public AccountClosedException(String accountId) {
        super("Account is closed: " + accountId);
    }
}
