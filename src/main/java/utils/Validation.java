package main.java.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Contains security and input validation methods
 */
public class Validation {
    // Password hashing with SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String inputPassword, String storedHash) {
        return hashPassword(inputPassword).equals(storedHash);
    }

    // Input validation
    public static String getValidInput(Scanner scanner, String prompt, String regex) {
        System.out.print(prompt);
        while (true) {
            String input = scanner.nextLine().trim();
            if (Pattern.matches(regex, input)) {
                return input;
            }
            System.out.println("Invalid input. Please try again.");
            System.out.print(prompt);
        }
    }

    public static int getValidInt(Scanner scanner, String prompt, int min, int max) {
        System.out.print(prompt);
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Continue to error message
            }
            System.out.printf("Please enter a number between %d and %d: ", min, max);
        }
    }

    public static double getValidDouble(Scanner scanner, String prompt, double min) {
        System.out.print(prompt);
        while (true) {
            try {
                double value = Double.parseDouble(scanner.nextLine());
                if (value >= min) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Continue to error message
            }
            System.out.printf("Please enter a number greater than %.2f: ", min);
        }
    }
}