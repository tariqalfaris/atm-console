package com.bank.atm.service;

import com.bank.atm.model.Account;

public class SufficientFundsRule implements WithdrawalRule {
    @Override
    public void validate(Account account, int amount) {
        if (amount > account.getBalance()) {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }
}
