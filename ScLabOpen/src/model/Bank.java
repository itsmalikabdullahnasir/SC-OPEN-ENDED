package com.scbanking.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.scbanking.exceptions.InvalidAccountException;
import com.scbanking.exceptions.InsufficientFundsException;
import com.scbanking.model.TransactionStatus;
import com.scbanking.model.TransactionType;


public class Bank {
    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<String, BankAccount> accounts = new HashMap<>();

    public Bank() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        Customer c1 = new Customer("CUST1001", "Alice", "1234");
        SavingsAccount s1 = new SavingsAccount("SA1001", c1.getCustomerId(), 1000.0, 100.0);
        CheckingAccount ch1 = new CheckingAccount("CA1001", c1.getCustomerId(), 200.0, 500.0);
        c1.addAccount(s1);
        c1.addAccount(ch1);
        customers.put(c1.getCustomerId(), c1);
        accounts.put(s1.getAccountNumber(), s1);
        accounts.put(ch1.getAccountNumber(), ch1);

        Customer c2 = new Customer("CUST1002", "Bob", "4321");
        SavingsAccount s2 = new SavingsAccount("SA1002", c2.getCustomerId(), 500.0, 50.0);
        c2.addAccount(s2);
        customers.put(c2.getCustomerId(), c2);
        accounts.put(s2.getAccountNumber(), s2);
    }

    public Customer getCustomerById(String id) { return customers.get(id); }
    public BankAccount getAccountByNumber(String acc) { return accounts.get(acc); }
    public Collection<Customer> getAllCustomers() { return customers.values(); }
    public Collection<BankAccount> getAllAccounts() { return accounts.values(); }

    public void registerCustomer(Customer c) { customers.put(c.getCustomerId(), c); }
    public void registerAccount(BankAccount a) {
        accounts.put(a.getAccountNumber(), a);
        Customer c = customers.get(a.getCustomerId());
        if (c != null) c.addAccount(a);
    }

    public void unblockCustomer(String customerId) {
        Customer c = customers.get(customerId);
        if (c != null) {
            c.setBlocked(false);
            c.resetFailedPinAttempts();
        }
    }

    /**
     * Transfer funds between two accounts within the bank.
     * Validates accounts exist and are ACTIVE. Throws InsufficientFundsException on failure.
     */
    public void transfer(String sourceAcc, String destAcc, double amount) throws InsufficientFundsException, InvalidAccountException {
        BankAccount s = accounts.get(sourceAcc);
        BankAccount d = accounts.get(destAcc);
        if (s == null || d == null) throw new InvalidAccountException("Source or destination account not found");
        if (s.getStatus() != AccountStatus.ACTIVE || d.getStatus() != AccountStatus.ACTIVE) throw new InvalidAccountException("Account not active");

        // attempt withdraw from source
        s.withdraw(amount);
        // deposit to destination
        d.deposit(amount);
        // add transfer transaction entries
        s.getTransactions().add(new Transaction(TransactionType.TRANSFER, amount, sourceAcc, destAcc, TransactionStatus.SUCCESS));
        d.getTransactions().add(new Transaction(TransactionType.TRANSFER, amount, sourceAcc, destAcc, TransactionStatus.SUCCESS));
    }
}
