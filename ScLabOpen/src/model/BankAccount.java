package com.scbanking.model;

import java.util.ArrayList;
import java.util.List;

import com.scbanking.exceptions.InsufficientFundsException;

/**
 * Abstract bank account with common behaviors.
 * Concrete subclasses must implement canWithdraw and getAccountType.
 */
public abstract class BankAccount {
    protected final String accountNumber;
    protected double balance;
    protected final String customerId;
    protected AccountStatus status;
    protected final List<Transaction> transactions = new ArrayList<>();

    public BankAccount(String accountNumber, String customerId, double initialBalance) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = initialBalance;
        this.status = AccountStatus.ACTIVE;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getCustomerId() { return customerId; }
    public AccountStatus getStatus() { return status; }
    public List<Transaction> getTransactions() { return transactions; }

    /**
     * Deposit common implementation. Adds a SUCCESS transaction and updates balance.
     */
    public Transaction deposit(double amount) {
        changeBalance(amount);
        Transaction t = new Transaction(TransactionType.DEPOSIT, amount, accountNumber, accountNumber, TransactionStatus.SUCCESS);
        transactions.add(t);
        return t;
    }

    /**
     * Withdraw common implementation. Delegates business rule check to canWithdraw.
     */
    public Transaction withdraw(double amount) throws InsufficientFundsException {
        if (!canWithdraw(amount)) {
            Transaction t = new Transaction(TransactionType.WITHDRAWAL, amount, accountNumber, null, TransactionStatus.FAILED_INSUFFICIENT_FUNDS);
            transactions.add(t);
            throw new InsufficientFundsException("Insufficient funds or account rule prevents withdrawal");
        }
        changeBalance(-amount);
        Transaction t = new Transaction(TransactionType.WITHDRAWAL, amount, accountNumber, null, TransactionStatus.SUCCESS);
        transactions.add(t);
        return t;
    }

    /**
     * Subclasses implement this to enforce minimum balance or overdraft rules.
     */
    protected abstract boolean canWithdraw(double amount);

    /**
     * Returns a human-readable account type (e.g., "SAVINGS", "CHECKING").
     */
    public abstract String getAccountType();

    /**
     * Change balance by delta. Package-protected so Bank can use it for transfers.
     */
    protected void changeBalance(double delta) { this.balance += delta; }
}
