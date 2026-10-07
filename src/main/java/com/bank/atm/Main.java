package com.bank.atm;

import com.bank.atm.repository.AccountRepository;
import com.bank.atm.repository.FirestoreAccountRepository;
import com.bank.atm.service.*;
import com.bank.atm.ui.ConsoleMenu;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        AccountRepository repository = new FirestoreAccountRepository("serviceAccountKey.json");

        TransactionHistory history = new InMemoryTransactionHistory();
        List<WithdrawalRule> rules = Arrays.asList(
                new SufficientFundsRule(),
                new DenominationRule(),
                new DailyLimitRule(history, 500));

        AccountService accountService = new AccountService(repository, history, rules);
        PinValidator pinValidator = new LockingPinValidator(repository);
        RegistrationService registrationService = new RegistrationService(repository);

        new ConsoleMenu(new Scanner(System.in), System.out, pinValidator, accountService, registrationService).run();
        System.exit(0); // closes the Firebase connection threads
    }
}
