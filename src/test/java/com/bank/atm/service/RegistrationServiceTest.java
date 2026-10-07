package com.bank.atm.service;

import com.bank.atm.model.Account;
import com.bank.atm.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private AccountRepository repository;

    @Test
    void createAccountSavesNewAccountWithZeroBalanceAndHashedPassword() {
        RegistrationService service = new RegistrationService(repository);

        Account account = service.createAccount("tariq", "secret1");

        assertEquals("tariq", account.getUsername());
        assertEquals(0, account.getBalance());
        assertNotEquals("secret1", account.getPasswordHash());
        assertTrue(account.passwordMatches(PasswordHasher.hash("tariq", "secret1")));
        verify(repository).save(account);
    }

    @Test
    void tooShortUsernameIsRejected() {
        RegistrationService service = new RegistrationService(repository);

        assertThrows(IllegalArgumentException.class, () -> service.createAccount("ab", "secret1"));
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    void usernameWithInvalidCharactersIsRejected() {
        RegistrationService service = new RegistrationService(repository);

        assertThrows(IllegalArgumentException.class, () -> service.createAccount("bad name", "secret1"));
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    void tooShortPasswordIsRejected() {
        RegistrationService service = new RegistrationService(repository);

        assertThrows(IllegalArgumentException.class, () -> service.createAccount("tariq", "123"));
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    void takenUsernameIsRejected() {
        when(repository.findByUsername("tariq")).thenReturn(new Account("tariq", "hash", 0));
        RegistrationService service = new RegistrationService(repository);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> service.createAccount("tariq", "secret1"));

        assertEquals("Username is already taken", e.getMessage());
        verify(repository, never()).save(any(Account.class));
    }
}
