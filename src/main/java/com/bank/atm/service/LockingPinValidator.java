package com.bank.atm.service;

import com.bank.atm.model.Account;
import com.bank.atm.repository.AccountRepository;

public class LockingPinValidator implements PinValidator {
    private static final int MAX_ATTEMPTS = 3;

    private final AccountRepository repository;

    public LockingPinValidator(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public LoginResult validate(String username, String password) {
        Account account = repository.findByUsername(username);
        if (account == null) {
            return LoginResult.NOT_FOUND;
        }
        if (account.isLocked()) {
            return LoginResult.LOCKED;
        }
        if (account.passwordMatches(PasswordHasher.hash(username, password))) {
            if (account.getFailedAttempts() > 0) {
                account.resetFailedAttempts();
                repository.save(account);
            }
            return LoginResult.SUCCESS;
        }
        account.recordFailedAttempt();
        if (account.getFailedAttempts() >= MAX_ATTEMPTS) {
            account.lock();
        }
        repository.save(account);
        return account.isLocked() ? LoginResult.LOCKED : LoginResult.WRONG_PASSWORD;
    }
}
