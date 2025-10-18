package com.scbanking;

import java.util.Scanner;

import com.scbanking.model.Bank;
import com.scbanking.ui.AdminConsole;
import com.scbanking.ui.ATM;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
    Scanner sc = new Scanner(System.in);
    ATM atm = new ATM(bank, sc);
    AdminConsole admin = new AdminConsole(bank, sc);
        boolean running = true;
        while (running) {
            System.out.println("Choose mode: 1-ATM 2-Admin 3-Exit");
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1": atm.start(); break;
                case "2": admin.start(); break;
                default: running = false; break;
            }
        }
        sc.close();
    }
}
