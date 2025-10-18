package com.scbanking.ui;

import java.util.List;
import java.util.Scanner;

import com.scbanking.exceptions.InsufficientFundsException;
import com.scbanking.model.Bank;
import com.scbanking.model.BankAccount;
import com.scbanking.model.Customer;
import com.scbanking.model.Transaction;

public class ATM {
    private final Bank bank;
    private final Scanner scanner;
    private static final int MAX_FAILED = 3;

    public ATM(Bank bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    public void start() {
    System.out.println("Welcome to SC Bank ATM");
    String id = InputUtil.readLine(scanner, "Enter customer ID: ");
        Customer c = bank.getCustomerById(id);
        if (c == null) { System.out.println("Customer not found."); return; }
        if (c.isBlocked()) { System.out.println("Customer is blocked. Contact admin."); return; }

    String pin = InputUtil.readLine(scanner, "Enter PIN: ");
        if (!c.getPin().equals(pin)) {
            c.incrementFailedPinAttempts();
            System.out.println("Incorrect PIN.");
            if (c.getFailedPinAttempts() >= MAX_FAILED) { c.setBlocked(true); System.out.println("Account blocked due to too many failed attempts."); }
            return;
        }
        c.resetFailedPinAttempts();

        BankAccount selected = selectAccount(c);
        if (selected == null) return;

        boolean done = false;
        while (!done) {
            System.out.println("Choose: 1-Balance 2-Deposit 3-Withdraw 4-Transactions 5-Transfer 6-Exit");
            String opt = InputUtil.readLine(scanner, "Option: ");
            switch (opt) {
                case "1":
                    System.out.printf("Balance: %.2f\n", selected.getBalance());
                    break;
                case "2":
                    double depAmt = InputUtil.readDouble(scanner, "Amount to deposit: ", 0.01);
                    Transaction td = selected.deposit(depAmt);
                    System.out.println("Deposit complete: " + td);
                    printReceipt(td, selected.getBalance());
                    break;
                case "3":
                    double wa = InputUtil.readDouble(scanner, "Amount to withdraw: ", 0.01);
                    try {
                        Transaction tw = selected.withdraw(wa);
                        System.out.println("Withdraw complete: " + tw);
                        printReceipt(tw, selected.getBalance());
                    } catch (InsufficientFundsException e) {
                        System.out.println("Failed: " + e.getMessage());
                    }
                    break;
                case "4":
                    List<Transaction> txs = selected.getTransactions();
                    if (txs.isEmpty()) System.out.println("No transactions.");
                    else txs.forEach(System.out::println);
                    break;
                case "5":
                    System.out.println("Transfer: 1-Within my accounts 2-To another account");
                    String tt = InputUtil.readLine(scanner, "Choice: ");
                    if ("1".equals(tt)) {
                        // list accounts excluding selected
                        Customer cust = bank.getCustomerById(c.getCustomerId());
                        List<BankAccount> options = new java.util.ArrayList<>();
                        for (BankAccount a : cust.getAccounts()) {
                            if (!a.getAccountNumber().equals(selected.getAccountNumber())) options.add(a);
                        }
                        if (options.isEmpty()) { System.out.println("No other accounts to transfer to."); break; }
                        System.out.println("Select destination account:");
                        for (int i = 0; i < options.size(); i++) {
                            System.out.printf("%d) %s (%.2f)\n", i+1, options.get(i).getAccountNumber(), options.get(i).getBalance());
                        }
                        int sel = InputUtil.readInt(scanner, "Choose destination (number): ", 1, options.size()) - 1;
                        BankAccount dest = options.get(sel);
                        double am = InputUtil.readDouble(scanner, "Amount: ", 0.01);
                        try {
                            bank.transfer(selected.getAccountNumber(), dest.getAccountNumber(), am);
                            System.out.println("Transfer successful");
                        } catch (Exception e) { System.out.println("Transfer failed: " + e.getMessage()); }
                    } else {
                        String destAccNo = InputUtil.readLine(scanner, "Destination account number: ");
                        double am = InputUtil.readDouble(scanner, "Amount: ", 0.01);
                        try {
                            bank.transfer(selected.getAccountNumber(), destAccNo, am);
                            System.out.println("Transfer successful");
                        } catch (Exception e) { System.out.println("Transfer failed: " + e.getMessage()); }
                    }
                    break;
                default:
                    done = true;
                    break;
            }
        }
    }

    private BankAccount selectAccount(Customer c) {
        List<BankAccount> a = c.getAccounts();
        if (a.isEmpty()) { System.out.println("No accounts."); return null; }
        if (a.size() == 1) return a.get(0);
        System.out.println("Select account:");
        for (int i = 0; i < a.size(); i++) {
            System.out.printf("%d) %s (%.2f)\n", i+1, a.get(i).getAccountNumber(), a.get(i).getBalance());
        }
        int sel = Integer.parseInt(scanner.nextLine()) - 1;
        if (sel < 0 || sel >= a.size()) { System.out.println("Invalid selection"); return null; }
        return a.get(sel);
    }

    private void printReceipt(Transaction t, double newBalance) {
        System.out.println("--- Receipt ---");
        System.out.println("Transaction ID: " + t.getId());
        System.out.println("Type: " + t.getType());
        System.out.println("Amount: " + t.getAmount());
        System.out.println("New Balance: " + newBalance);
        System.out.println("Status: " + t.getStatus());
        System.out.println("----------------");
    }
}
