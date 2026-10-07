package com.bank.atm.service;

import com.bank.atm.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTransactionHistory implements TransactionHistory {
    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void add(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> getAll(String username) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getUsername().equals(username)) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public List<Transaction> getLast(String username, int count) {
        List<Transaction> all = getAll(username);
        int from = Math.max(0, all.size() - count);
        return new ArrayList<>(all.subList(from, all.size()));
    }
}
