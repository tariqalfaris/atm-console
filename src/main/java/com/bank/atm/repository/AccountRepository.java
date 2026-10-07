package com.bank.atm.repository;

import com.bank.atm.model.Account;

public interface AccountRepository {
    /** Returns the account, or null if no account has that username. */
    Account findByUsername(String username);

    /** Creates the account if it is new, or updates it if it already exists. */
    void save(Account account);
}
