package com.bank.atm.integration;

import com.bank.atm.model.Transaction;
import com.bank.atm.repository.AccountRepository;
import com.bank.atm.repository.InMemoryAccountRepository;
import com.bank.atm.service.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AtmFlowIntegrationTest {

    @Test
    void createAccountLogInDepositWithdrawCheckBalanceAndStatement() {
        AccountRepository repository = new InMemoryAccountRepository();
        TransactionHistory history = new InMemoryTransactionHistory();
        AccountService service = new AccountService(repository, history, Arrays.asList(
                new SufficientFundsRule(),
                new DenominationRule(),
                new DailyLimitRule(history, 500)));
        PinValidator validator = new LockingPinValidator(repository);
        RegistrationService registration = new RegistrationService(repository);

        registration.createAccount("tariq", "secret1");

        assertEquals(LoginResult.SUCCESS, validator.validate("tariq", "secret1"));
        service.deposit("tariq", 200);
        service.withdraw("tariq", 150);

        assertEquals(50, service.checkBalance("tariq"));

        List<Transaction> statement = service.getMiniStatement("tariq");
        assertEquals(2, statement.size());
        assertEquals(Transaction.Type.DEPOSIT, statement.get(0).getType());
        assertEquals(200, statement.get(0).getAmount());
        assertEquals(Transaction.Type.WITHDRAWAL, statement.get(1).getType());
        assertEquals(150, statement.get(1).getAmount());
    }
}
