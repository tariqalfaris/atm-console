package com.bank.atm.repository;

import com.bank.atm.model.Account;

import java.util.HashMap;
import java.util.Map;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> accounts = new HashMap<>();

    @Override
    public Account findByUsername(String username) {
        return accounts.get(username);
    }

    @Override
    public void save(Account account) {
        accounts.put(account.getUsername(), account);
    }
}
