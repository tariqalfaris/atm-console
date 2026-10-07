package com.bank.atm.ui;

import com.bank.atm.model.Account;
import com.bank.atm.model.Transaction;
import com.bank.atm.service.AccountService;
import com.bank.atm.service.LoginResult;
import com.bank.atm.service.PinValidator;
import com.bank.atm.service.RegistrationService;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner in;
    private final PrintStream out;
    private final PinValidator pinValidator;
    private final AccountService accountService;
    private final RegistrationService registrationService;

    public ConsoleMenu(Scanner in, PrintStream out, PinValidator pinValidator,
                       AccountService accountService, RegistrationService registrationService) {
        this.in = in;
        this.out = out;
        this.pinValidator = pinValidator;
        this.accountService = accountService;
        this.registrationService = registrationService;
    }

    public void run() {
        while (true) {
            out.println();
            out.println("1. Log In");
            out.println("2. Create Account");
            out.println("3. Exit");
            out.print("Choose an option: ");
            String choice = in.hasNextLine() ? in.nextLine().trim() : "3";

            switch (choice) {
                case "1":
                    logIn();
                    break;
                case "2":
                    createAccount();
                    break;
                case "3":
                    out.println("Goodbye.");
                    return;
                default:
                    out.println("Invalid option.");
            }
        }
    }

    private void logIn() {
        String username = prompt("Username: ");
        String password = prompt("Password: ");
        LoginResult result = pinValidator.validate(username, password);

        switch (result) {
            case SUCCESS:
                accountMenu(username);
                break;
            case WRONG_PASSWORD:
                out.println("Incorrect password.");
                break;
            case LOCKED:
                out.println("Account is locked.");
                break;
            default:
                out.println("Username not found.");
        }
    }

    private void createAccount() {
        String username = prompt("Choose a username: ");
        String password = prompt("Choose a password: ");
        try {
            Account account = registrationService.createAccount(username, password);
            out.println("Account created. You can now log in as " + account.getUsername());
        } catch (IllegalArgumentException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void accountMenu(String username) {
        while (true) {
            out.println();
            out.println("1. Check Balance");
            out.println("2. Deposit");
            out.println("3. Withdraw");
            out.println("4. Mini Statement");
            out.println("5. Log Out");
            out.print("Choose an option: ");
            String choice = in.hasNextLine() ? in.nextLine().trim() : "5";

            switch (choice) {
                case "1":
                    out.println("Balance: $" + accountService.checkBalance(username));
                    break;
                case "2":
                    deposit(username);
                    break;
                case "3":
                    withdraw(username);
                    break;
                case "4":
                    printMiniStatement(username);
                    break;
                case "5":
                    out.println("Logged out.");
                    return;
                default:
                    out.println("Invalid option.");
            }
        }
    }

    private void deposit(String username) {
        Integer amount = readAmount("Amount to deposit: ");
        if (amount == null) {
            return;
        }
        try {
            accountService.deposit(username, amount);
            out.println("Deposit successful.");
        } catch (IllegalArgumentException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void withdraw(String username) {
        Integer amount = readAmount("Amount to withdraw: ");
        if (amount == null) {
            return;
        }
        try {
            accountService.withdraw(username, amount);
            out.println("Please take your cash.");
        } catch (IllegalArgumentException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void printMiniStatement(String username) {
        List<Transaction> transactions = accountService.getMiniStatement(username);
        if (transactions.isEmpty()) {
            out.println("No transactions yet.");
            return;
        }
        for (Transaction t : transactions) {
            out.println(t);
        }
    }

    private String prompt(String text) {
        out.print(text);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    private Integer readAmount(String text) {
        try {
            return Integer.parseInt(prompt(text));
        } catch (NumberFormatException e) {
            out.println("Please enter a whole number.");
            return null;
        }
    }
}
