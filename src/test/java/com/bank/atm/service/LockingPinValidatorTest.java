package com.bank.atm.service;

import com.bank.atm.model.Account;
import com.bank.atm.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class LockingPinValidatorTest {
    private static final String USER = "tariq";
    private static final String PASSWORD = "secret1";

    @Mock
    private AccountRepository repository;

    private Account account;
    private LockingPinValidator validator;

    @BeforeEach
    void setUp() {
        account = new Account(USER, PasswordHasher.hash(USER, PASSWORD), 1000);
        lenient().when(repository.findByUsername(USER)).thenReturn(account);
        validator = new LockingPinValidator(repository);
    }

    @Test
    void correctPasswordSucceeds() {
        assertEquals(LoginResult.SUCCESS, validator.validate(USER, PASSWORD));
    }

    @Test
    void wrongPasswordIsRejectedWithoutLocking() {
        assertEquals(LoginResult.WRONG_PASSWORD, validator.validate(USER, "wrong"));
        assertFalse(account.isLocked());
    }

    @Test
    void accountLocksAfterThreeFailedAttempts() {
        assertEquals(LoginResult.WRONG_PASSWORD, validator.validate(USER, "wrong1"));
        assertEquals(LoginResult.WRONG_PASSWORD, validator.validate(USER, "wrong2"));
        assertEquals(LoginResult.LOCKED, validator.validate(USER, "wrong3"));

        assertTrue(account.isLocked());
    }

    @Test
    void correctPasswordIsRejectedOnceLocked() {
        validator.validate(USER, "wrong1");
        validator.validate(USER, "wrong2");
        validator.validate(USER, "wrong3");

        assertEquals(LoginResult.LOCKED, validator.validate(USER, PASSWORD));
    }

    @Test
    void correctPasswordResetsFailedAttempts() {
        validator.validate(USER, "wrong1");
        validator.validate(USER, PASSWORD);
        validator.validate(USER, "wrong2");
        validator.validate(USER, "wrong3");

        assertFalse(account.isLocked());
    }

    @Test
    void unknownUsernameIsNotFound() {
        assertEquals(LoginResult.NOT_FOUND, validator.validate("nobody", PASSWORD));
    }
}
