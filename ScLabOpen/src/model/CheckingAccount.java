package com.scbanking.model;

import com.scbanking.exceptions.InsufficientFundsException;

public class CheckingAccount extends BankAccount {
    private final double overdraftLimit;

    public CheckingAccount(String accountNumber, String customerId, double initialBalance, double overdraftLimit) {
        super(accountNumber, customerId, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }
    @Override
    protected boolean canWithdraw(double amount) {
        return (balance - amount) >= -overdraftLimit;
    }

    @Override
    public String getAccountType() { return "CHECKING"; }
}
