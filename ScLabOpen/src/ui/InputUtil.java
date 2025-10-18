package com.scbanking.ui;

import java.util.Scanner;

public final class InputUtil {
    private InputUtil() {}

    public static String readLine(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public static double readDouble(Scanner sc, String prompt, double min) {
        while (true) {
            try {
                System.out.print(prompt);
                String s = sc.nextLine().trim();
                double v = Double.parseDouble(s);
                if (v < min) { System.out.println("Value must be >= " + min); continue; }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    public static int readInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                String s = sc.nextLine().trim();
                int v = Integer.parseInt(s);
                if (v < min || v > max) { System.out.println("Value must be between " + min + " and " + max); continue; }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer, try again.");
            }
        }
    }
}
