package com.bank.atm.model;

public class Account {
    private final String username;
    private final String passwordHash;
    private int balance;
    private boolean locked;
    private int failedAttempts;

    public Account(String username, String passwordHash, int balance) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.balance = balance;
    }

    public Account(String username, String passwordHash, int balance, boolean locked, int failedAttempts) {
        this(username, passwordHash, balance);
        this.locked = locked;
        this.failedAttempts = failedAttempts;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getBalance() {
        return balance;
    }

    public boolean passwordMatches(String hash) {
        return passwordHash != null && passwordHash.equals(hash);
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        locked = true;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void recordFailedAttempt() {
        failedAttempts++;
    }

    public void resetFailedAttempts() {
        failedAttempts = 0;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }
}
