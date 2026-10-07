package com.bank.atm.service;

import com.bank.atm.model.Account;
import com.bank.atm.model.Transaction;
import com.bank.atm.repository.AccountRepository;

import java.time.LocalDate;
import java.util.List;

public class AccountService {
    private static final int MINI_STATEMENT_SIZE = 5;

    private final AccountRepository repository;
    private final TransactionHistory history;
    private final List<WithdrawalRule> rules;

    public AccountService(AccountRepository repository, TransactionHistory history, List<WithdrawalRule> rules) {
        this.repository = repository;
        this.history = history;
        this.rules = rules;
    }

    public int checkBalance(String username) {
        return load(username).getBalance();
    }

    public void deposit(String username, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        Account account = load(username);
        account.deposit(amount);
        repository.save(account);
        history.add(new Transaction(username, Transaction.Type.DEPOSIT, amount, LocalDate.now()));
    }

    public void withdraw(String username, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }
        Account account = load(username);
        for (WithdrawalRule rule : rules) {
            rule.validate(account, amount);
        }
        account.withdraw(amount);
        repository.save(account);
        history.add(new Transaction(username, Transaction.Type.WITHDRAWAL, amount, LocalDate.now()));
    }

    public List<Transaction> getMiniStatement(String username) {
        return history.getLast(username, MINI_STATEMENT_SIZE);
    }

    private Account load(String username) {
        Account account = repository.findByUsername(username);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        return account;
    }
}
