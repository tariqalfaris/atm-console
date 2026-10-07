package com.bank.atm.service;

import com.bank.atm.model.Account;
import com.bank.atm.model.Transaction;
import com.bank.atm.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    private static final String USER = "tariq";

    @Mock
    private AccountRepository repository;
    @Mock
    private TransactionHistory history;

    private Account account;
    private AccountService service;

    @BeforeEach
    void setUp() {
        account = new Account(USER, "hash", 1000);
        lenient().when(repository.findByUsername(USER)).thenReturn(account);
        service = new AccountService(repository, history, Arrays.asList(
                new SufficientFundsRule(),
                new DenominationRule(),
                new DailyLimitRule(history, 500)));
    }

    @Test
    void depositIncreasesBalanceSavesAndRecordsTransaction() {
        service.deposit(USER, 200);

        assertEquals(1200, service.checkBalance(USER));
        verify(repository).save(account);
        verify(history).add(any(Transaction.class));
    }

    @Test
    void negativeDepositIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.deposit(USER, -50));
        assertEquals(1000, account.getBalance());
        verify(history, never()).add(any(Transaction.class));
    }

    @Test
    void zeroDepositIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.deposit(USER, 0));
        assertEquals(1000, account.getBalance());
    }

    @Test
    void withdrawReducesBalanceSavesAndRecordsTransaction() {
        service.withdraw(USER, 100);

        assertEquals(900, account.getBalance());
        verify(repository).save(account);
        verify(history).add(any(Transaction.class));
    }

    @Test
    void withdrawalOverBalanceIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> service.withdraw(USER, 1100));

        assertEquals("Insufficient funds", e.getMessage());
        assertEquals(1000, account.getBalance());
    }

    @Test
    void withdrawalNotMultipleOfTenIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.withdraw(USER, 55));
        assertEquals(1000, account.getBalance());
    }

    @Test
    void withdrawalOverDailyLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.withdraw(USER, 600));
        assertEquals(1000, account.getBalance());
    }

    @Test
    void dailyLimitCountsEarlierWithdrawalsToday() {
        lenient().when(history.getAll(USER)).thenReturn(Collections.singletonList(
                new Transaction(USER, Transaction.Type.WITHDRAWAL, 400, LocalDate.now())));

        assertThrows(IllegalArgumentException.class, () -> service.withdraw(USER, 200));
        assertEquals(1000, account.getBalance());
    }

    @Test
    void zeroWithdrawalIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.withdraw(USER, 0));
    }

    @Test
    void unknownAccountIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> service.deposit("nobody", 100));

        assertEquals("Account not found", e.getMessage());
    }
}
