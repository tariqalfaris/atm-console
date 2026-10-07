package com.bank.atm.service;

import com.bank.atm.model.Account;
import com.bank.atm.repository.AccountRepository;

public class RegistrationService {
    private final AccountRepository repository;

    public RegistrationService(AccountRepository repository) {
        this.repository = repository;
    }

    /** Creates a new account with a balance of 0. */
    public Account createAccount(String username, String password) {
        if (username == null || !username.matches("[A-Za-z0-9_]{3,20}")) {
            throw new IllegalArgumentException("Username must be 3-20 letters, numbers or underscores");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
        if (repository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username is already taken");
        }
        Account account = new Account(username, PasswordHasher.hash(username, password), 0);
        repository.save(account);
        return account;
    }
}
