package com.bank.atm.service;

import com.bank.atm.model.Account;

public class DenominationRule implements WithdrawalRule {
    @Override
    public void validate(Account account, int amount) {
        if (amount % 10 != 0) {
            throw new IllegalArgumentException("Amount must be a multiple of $10");
        }
    }
}
