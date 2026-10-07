package com.bank.atm.model;

import java.time.LocalDate;

public class Transaction {
    public enum Type { DEPOSIT, WITHDRAWAL }

    private final String username;
    private final Type type;
    private final int amount;
    private final LocalDate date;

    public Transaction(String username, Type type, int amount, LocalDate date) {
        this.username = username;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public Type getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date + "  " + type + "  $" + amount;
    }
}
