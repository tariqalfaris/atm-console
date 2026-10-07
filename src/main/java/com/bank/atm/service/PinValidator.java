package com.bank.atm.service;

/** Checks the login credentials (username and password) of an account. */
public interface PinValidator {
    LoginResult validate(String username, String password);
}
