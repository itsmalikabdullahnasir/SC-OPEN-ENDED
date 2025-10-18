package com.scbanking.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private final String customerId;
    private final String name;
    private String pin;
    private boolean blocked = false;
    private int failedPinAttempts = 0;
    private final List<BankAccount> accounts = new ArrayList<>();

    public Customer(String customerId, String name, String pin) {
        this.customerId = customerId;
        this.name = name;
        this.pin = pin;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public int getFailedPinAttempts() { return failedPinAttempts; }
    public void incrementFailedPinAttempts() { this.failedPinAttempts++; }
    public void resetFailedPinAttempts() { this.failedPinAttempts = 0; }

    public List<BankAccount> getAccounts() { return accounts; }
    public void addAccount(BankAccount a) { accounts.add(a); }
}
