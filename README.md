Advanced Banking System — Design Document

1. Overview
This document describes the architecture and design decisions for the Advanced Banking System implemented in Java. The system simulates a Bank managing multiple Customers and their BankAccounts, exposes a text-based ATM interface for customers and a separate Admin console for administrators. The goals for this lab are clarity of design, correctness of business rules, maintainability, and extensibility.

2. High-level Architecture
- Presentation layer: CLI components (`ATM`, `AdminConsole`) handle user interaction and input validation.
- Domain/Service layer: `Bank`, `Customer`, `BankAccount` and its subclasses implement business logic (deposits, withdrawals, transfers, account rules).
- Persistence: in-memory for the lab (HashMaps, ArrayLists). The design isolates data access so a persistence adapter (JSON/DB) can be added later.

3. Class Diagram (textual / UML-like)

Bank
 - customers: HashMap<String, Customer>
 - accounts: HashMap<String, BankAccount>
 + registerCustomer(c: Customer)
 + registerAccount(a: BankAccount)
 + getCustomerById(id: String): Customer
 + getAccountByNumber(acc: String): BankAccount
 + transfer(sourceAcc: String, destAcc: String, amount: double)
 + unblockCustomer(customerId: String)

Customer
 - customerId: String
 - name: String
 - pin: String
 - blocked: boolean
 - failedPinAttempts: int
 - accounts: ArrayList<BankAccount>
 + addAccount(a: BankAccount)
 + resetFailedPinAttempts()

BankAccount (abstract)
 - accountNumber: String
 - balance: double
 - customerId: String
 - status: AccountStatus
 - transactions: ArrayList<Transaction>
 + deposit(amount: double): Transaction
 + withdraw(amount: double): Transaction throws InsufficientFundsException
 - canWithdraw(amount: double): boolean  // subclass responsibility
 - changeBalance(delta: double)
 + getAccountType(): String

SavingsAccount extends BankAccount
 - minimumBalance: double
 + canWithdraw(amount): boolean  // enforces minimum balance

CheckingAccount extends BankAccount
 - overdraftLimit: double
 + canWithdraw(amount): boolean  // enforces overdraft limit

Transaction
 - id: String (UUID)
 - type: TransactionType (DEPOSIT/WITHDRAWAL/TRANSFER)
 - amount: double
 - timestamp: LocalDateTime
 - sourceAccount: String
 - destinationAccount: String
 - status: TransactionStatus

Enums: `TransactionType`, `TransactionStatus`, `AccountStatus`

4. Data Structures & Rationale

- Bank-level maps
	- `customers: HashMap<String, Customer>` and `accounts: HashMap<String, BankAccount>` are used to provide O(1) lookup by ID/account number. This is important for admin operations and efficient transfer validation.

- Per-account transaction history
	- `transactions: ArrayList<Transaction>`: chosen for ordered appends and efficient iteration when displaying statements. The list preserves chronological order by insertion.

5. Object-Oriented Design & Patterns

- Encapsulation & Single Responsibility
	- Each class has a well-scoped responsibility: `Bank` orchestrates and stores data; `Customer` manages personal data and associated accounts; `BankAccount` (and children) manage account state and business rules.

- Polymorphism
	- `BankAccount` exposes common operations (`deposit`, `withdraw`) while delegating account-specific policy checks (`canWithdraw`) to subclasses. This allows the `ATM` and `Bank` components to work with `BankAccount` references without knowing concrete types.

- Transaction Auditing
	- Every financial action creates a `Transaction` object. Transfers create synchronous entries on both source and destination accounts to ensure auditability.

6. Error Handling & Validation

- Business exceptions
	- `InsufficientFundsException`: thrown when a withdrawal or transfer violates account constraints.
	- `InvalidAccountException`: used when an account/customer lookup fails (API-level validation).
	- `AccountBlockedException`: reserved for blocked account access.

- CLI validation
	- `InputUtil` centralizes input parsing and basic bounds checking to reduce duplication and improve robustness.

7. Security Considerations

- Authentication & PIN handling
	- PINs are stored in-memory as plain strings for the lab. In production, always store salted hashed PINs and use secure input handling.

- Brute-force protection
	- The system blocks a customer after 3 consecutive failed PIN attempts. Blocking is reset by an administrator.

- Admin credentials
	- Admin credentials are hardcoded for this assignment. For real systems, use secure credential management and role-based access control.

8. Transaction Lifecycle (sequence)

1. User issues a command in `ATM` (deposit/withdraw/transfer).
2. `ATM` validates inputs using `InputUtil`.
3. `ATM` invokes domain operations on `Bank` or `BankAccount`.
4. `BankAccount` performs `canWithdraw` checks (if applicable), then updates balance with `changeBalance` and appends a `Transaction` to its history with appropriate status.
5. For transfers, `Bank.transfer(...)` orchestrates the withdraw and deposit and appends transfer transactions to both accounts.

9. Extensibility and Next Steps

- Persistence adapter
	- Implement a persistence layer (JSON files or SQL DB) and add a repository/service layer that reads/writes `Bank`, `Customer`, and `Transaction` objects on startup/shutdown.

- Concurrency
	- Protect account-level operations using synchronization or account-level locks when the app is extended to multi-threaded or networked front-ends.

- Additional features
	- Interest calculation for `SavingsAccount` (periodic job);
	- Transaction fees for `CheckingAccount`;
	- Enhanced admin features (reports, bulk import);
	- Unit tests (JUnit) for core behaviors.

10. How to run (recap)
Compile and run from the project root (PowerShell):
```powershell
javac -d out -sourcepath src src/com/scbanking/Main.java
java -cp out com.scbanking.Main
```

Sample credentials (provided data):
- Customer: `CUST1001` / PIN `1234` (Alice)
- Customer: `CUST1002` / PIN `4321` (Bob)
- Admin: `admin` / `password`

11. Closing notes
This implementation focuses on clear object-oriented structure, simple but robust CLI interfaces, and modular components that make it straightforward to add persistence, concurrency controls, and additional account types. The code base is suitable as a submission for the lab and as a starting point for further enhancements.

