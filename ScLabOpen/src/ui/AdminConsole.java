package com.scbanking.ui;

import java.util.Scanner;

import com.scbanking.model.Bank;
import com.scbanking.model.BankAccount;
import com.scbanking.model.Customer;

public class AdminConsole {
    private final Bank bank;
    private final Scanner scanner;

    public AdminConsole(Bank bank, Scanner scanner) { this.bank = bank; this.scanner = scanner; }

    public void start() {
        System.out.print("Admin user: ");
        String user = scanner.nextLine().trim();
        System.out.print("Admin pass: ");
        String pass = scanner.nextLine().trim();
        if (!"admin".equals(user) || !"password".equals(pass)) { System.out.println("Invalid admin credentials"); return; }

        boolean done = false;
        while (!done) {
            System.out.println("Admin: 1-ViewCustomers 2-ViewAccounts 3-CreateCustomer 4-CreateAccount 5-UnblockCustomer 6-Exit");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1":
                    for (Customer c : bank.getAllCustomers()) {
                        System.out.printf("%s - %s accounts=%d\n", c.getCustomerId(), c.getName(), c.getAccounts().size());
                    }
                    break;
                case "2":
                    for (BankAccount a : bank.getAllAccounts()) {
                        System.out.printf("%s %s %.2f\n", a.getAccountNumber(), a.getCustomerId(), a.getBalance());
                    }
                    break;
                case "3":
                    System.out.print("New Customer ID: ");
                    String newId = scanner.nextLine().trim();
                    if (newId.isEmpty()) { System.out.println("Customer ID cannot be empty"); break; }
                    if (bank.getCustomerById(newId) != null) { System.out.println("Customer ID already exists"); break; }
                    System.out.print("Customer name: ");
                    String newName = scanner.nextLine().trim();
                    System.out.print("Customer PIN: ");
                    String newPin = scanner.nextLine().trim();
                    Customer newCustomer = new Customer(newId, newName, newPin);
                    bank.registerCustomer(newCustomer);
                    System.out.println("Customer created: " + newId);
                    break;
                case "4":
                    System.out.print("Customer ID: ");
                    String cid = scanner.nextLine().trim();
                    Customer c = bank.getCustomerById(cid);
                    if (c == null) { System.out.println("Customer not found"); break; }
                    System.out.print("Type (S/C): ");
                    String t = scanner.nextLine().trim();
                    System.out.print("Account number: ");
                    String an = scanner.nextLine().trim();
                    System.out.print("Initial balance: ");
                    double ib = Double.parseDouble(scanner.nextLine());
                    if (t.equalsIgnoreCase("S")) {
                        bank.registerAccount(new com.scbanking.model.SavingsAccount(an, cid, ib, 50.0));
                    } else {
                        bank.registerAccount(new com.scbanking.model.CheckingAccount(an, cid, ib, 500.0));
                    }
                    System.out.println("Account created");
                    break;
                case "5":
                    System.out.print("Customer ID to unblock: ");
                    String uc = scanner.nextLine().trim();
                    bank.unblockCustomer(uc);
                    System.out.println("Done");
                    break;
                default:
                    done = true;
                    break;
            }
        }
    }
}
