# ATM Console Application

A console ATM simulator (Java, Maven, JUnit 5, Mockito). Users can create an account and log in with a username and password (stored as a SHA-256 hash). Accounts are stored in Firebase Firestore (collection `accounts`, one document per username). Transaction history is kept in memory.

Run tests: `mvn test`
Run the app: put `serviceAccountKey.json` (Firebase service account key) in the project folder, then run `mvn compile exec:java -Dexec.mainClass=com.bank.atm.Main` (or run `Main` from your IDE)

## Class structure

- `model`: `Account` (username, password hash, balance, locked flag, failed attempts), `Transaction` (username, type, amount, date)
- `repository`: `AccountRepository` interface, `FirestoreAccountRepository` (used by `Main`) and `InMemoryAccountRepository` (used in the integration test)
- `service`: `AccountService` (balance, deposit, withdraw, mini statement), `RegistrationService` (create account), `PinValidator` / `LockingPinValidator` (log in, lock after 3 wrong passwords), `PasswordHasher`, `LoginResult`, `TransactionHistory` / `InMemoryTransactionHistory`, `WithdrawalRule` and its rules
- `ui`: `ConsoleMenu` (input/output only)
- `Main` wires everything together

## SOLID

- **S**: Each class has one job: `Account` holds account data, `LockingPinValidator` handles login and locking, `RegistrationService` creates accounts, `PasswordHasher` hashes passwords, `AccountService` handles money operations, `ConsoleMenu` only does console I/O. There is no ATM god-class.
- **O**: Withdrawal checks implement `WithdrawalRule`. To add a new rule, create a new class and add it to the list passed to `AccountService`; `SufficientFundsRule`, `DenominationRule` and `DailyLimitRule` do not change.
- **L**: There is no inheritance in `Account`, so nothing can break substitution. `FirestoreAccountRepository` and `InMemoryAccountRepository` follow the same `AccountRepository` contract (`findByUsername` returns null when missing, `save` creates or updates), and every `WithdrawalRule` follows the same contract, so each can replace another without breaking the services.
- **I**: Interfaces are small and role-specific: `PinValidator` has only `validate`, `TransactionHistory` has only history methods, `AccountRepository` only finds and saves accounts.
- **D**: `AccountService`, `LockingPinValidator` and `RegistrationService` depend on the `AccountRepository` interface, and `ConsoleMenu` depends on `PinValidator`, not on concrete classes. The unit tests mock these interfaces with Mockito, `Main` passes in `FirestoreAccountRepository`, and the integration test passes in `InMemoryAccountRepository`.
