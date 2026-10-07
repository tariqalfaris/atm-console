package com.bank.atm.service;

import com.bank.atm.model.Transaction;

import java.util.List;

public interface TransactionHistory {
    void add(Transaction transaction);

    List<Transaction> getAll(String username);

    List<Transaction> getLast(String username, int count);
}
