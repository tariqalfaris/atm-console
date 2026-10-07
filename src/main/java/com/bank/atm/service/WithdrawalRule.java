package com.bank.atm.service;

import com.bank.atm.model.Account;

public interface WithdrawalRule {
    /** Throws IllegalArgumentException with a message if the withdrawal is not allowed. */
    void validate(Account account, int amount);
}
