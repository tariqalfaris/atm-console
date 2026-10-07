package com.bank.atm.service;

import com.bank.atm.model.Account;
import com.bank.atm.model.Transaction;

import java.time.LocalDate;

public class DailyLimitRule implements WithdrawalRule {
    private final TransactionHistory history;
    private final int dailyLimit;

    public DailyLimitRule(TransactionHistory history, int dailyLimit) {
        this.history = history;
        this.dailyLimit = dailyLimit;
    }

    @Override
    public void validate(Account account, int amount) {
        int withdrawnToday = 0;
        for (Transaction t : history.getAll(account.getUsername())) {
            if (t.getType() == Transaction.Type.WITHDRAWAL && t.getDate().equals(LocalDate.now())) {
                withdrawnToday += t.getAmount();
            }
        }
        if (withdrawnToday + amount > dailyLimit) {
            throw new IllegalArgumentException("Daily withdrawal limit of $" + dailyLimit + " exceeded");
        }
    }
}
