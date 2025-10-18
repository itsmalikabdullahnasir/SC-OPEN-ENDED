package com.scbanking.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final String id;
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String sourceAccount;
    private final String destinationAccount;
    private final TransactionStatus status;

    public Transaction(TransactionType type, double amount, String sourceAccount, String destinationAccount, TransactionStatus status) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.status = status;
    }

    public String getId() { return id; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getSourceAccount() { return sourceAccount; }
    public String getDestinationAccount() { return destinationAccount; }
    public TransactionStatus getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f from=%s to=%s status=%s",
                timestamp, type, amount, sourceAccount, destinationAccount, status);
    }
}
