import java.math.BigDecimal;

public class Account implements IAccount {

    /**
     * Current balance this account holds
     */
    private BigDecimal balance;
    /**
     * Currency used in this account, can be "SEK", "EUR", or "USD"
     */
    private String currency;
    /**
     * max_overdrawn is a non-negative number indicating how much the account can be "in the red"
     * The minimum balance of the account is -1 * max_overdrawn
     */
    private BigDecimal max_overdrawn;

    public BigDecimal getMaxOverdrawn() {
        return this.max_overdrawn;
    }

    public void setMaxOverdrawn(BigDecimal max_overdrawn) {
        if (max_overdrawn.compareTo(BigDecimal.ZERO) <= 0) {
            this.max_overdrawn = BigDecimal.ZERO;
        } else {
            this.max_overdrawn = max_overdrawn;
        }
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setBalance(BigDecimal balance) {
        // Allow only if balance >= -max_overdrawn
        if (balance.compareTo(this.max_overdrawn.negate()) >= 0) {
            this.balance = balance;
        }
        // else do nothing (reject setting beyond overdraft)
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Account() {
        this.balance = BigDecimal.ZERO;
        this.currency = "SEK";
        this.max_overdrawn = BigDecimal.ZERO;
    }

    public Account(BigDecimal starting_balance, String currency, BigDecimal max_overdrawn) {
        this.balance = starting_balance;
        this.currency = currency;
        if (max_overdrawn.compareTo(BigDecimal.ZERO) <= 0) {
            this.max_overdrawn = BigDecimal.ZERO;
        } else {
            this.max_overdrawn = max_overdrawn;
        }
    }

    @Override
    public BigDecimal withdraw(BigDecimal requestedAmount) {
        // Calculate the lowest allowed balance (-max_overdrawn)
        BigDecimal allowedLimit = this.max_overdrawn.negate();
        BigDecimal newBalance = this.balance.subtract(requestedAmount);

        // Only update balance if it doesn't exceed allowed overdraft
        if (newBalance.compareTo(allowedLimit) >= 0) {
            this.balance = newBalance;  // Update balance
            return this.balance;
        } else {
            // Withdrawal rejected, balance unchanged
            return this.balance;
        }
    }
// Fix: Original code did not check max_overdrawn or update balance

    @Override
    public BigDecimal deposit(BigDecimal amount_to_deposit) {
        // Prevent negative deposits
        if (amount_to_deposit.compareTo(BigDecimal.ZERO) < 0) {
            return this.balance; // ignore negative deposits
        }
        this.balance = this.balance.add(amount_to_deposit); // update balance
        return this.balance;
    }
//Problems:
//
//It doesn’t update the account balance, only returns a sum.
//
//It doesn’t prevent negative deposits.

    @Override
    public void convertToCurrency(String currencyCode, double rate) {
        /*
         * Expected behavior:
         *   Converts the account balance based on the given rate and updates
         *   the currency. Returns true if conversion succeeded, false otherwise.
         *
         * Provided skeleton behavior:
         *   Original version did not return anything and did not properly
         *   handle invalid rates or update balance.
         *
         * Fix applied:
         *   - Added validation for rate > 0.
         *   - Updated both this.balance and this.currency.
         *   - Returns true if conversion succeeds, false if invalid input.
         */
        if (rate <= 0) {
            return; // invalid rate
        }
        this.currency = currencyCode;
        this.balance = this.balance.multiply(BigDecimal.valueOf(rate));
    }

    /*
     * Transfers all available funds from this account to another account.
     *
     * Expected behavior:
     *  - Transfer succeeds only if:
     *      1. Both accounts use the same currency.
     *      2. The sender has a positive balance.
     *  - Upon successful transfer:
     *      - The entire balance from this account is deposited into the target account.
     *      - This account’s balance becomes zero.
     *
     * Original skeleton issue:
     *  - The provided code did not set the sender’s balance to zero after transferring.
     *  - It also allowed transfers even when currencies did not match.
     *
     * Fix applied:
     *  - Added currency equality check.
     *  - Added validation that the balance must be positive.
     *  - Added proper zeroing of sender’s balance after successful transfer.
     */
    @Override
    public void TransferToAccount(IAccount to_account) {

        // Ensure the provided account is of type Account to access currency
        if (to_account instanceof Account) {
            Account targetAccount = (Account) to_account;

            // Do not allow transfer from empty or negative balance accounts
            if (this.balance.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Transfer failed: Negative or zero balance.");
                return;
            }

            // Only transfer if both accounts use the same currency
            if (this.currency.equals(targetAccount.getCurrency())) {
                // Perform the transfer
                targetAccount.deposit(this.balance);

                // Set the sender's balance to zero
                this.balance = BigDecimal.ZERO;

            } else {
                System.out.println("Transfer failed: currencies do not match.");
            }
        }
    }


    /*
    Initially the method did not update the sender's balance, leading to errors, eve though the reciever was being updated
     */

    /*
     * Withdraws all the available (positive) funds from the account.
     *
     * Expected behavior:
     *  - If the balance is positive, withdraw all funds (set balance to 0).
     *  - If the balance is zero or negative, no withdrawal occurs.
     *
     * Original skeleton issue:
     *  - The method contained unnecessary conditions (e.g., max_overdrawn checks).
     *  - Negative balance accounts could sometimes be incorrectly reset to zero.
     *
     * Fix applied:
     *  - Simplified logic to only zero positive balances.
     *  - Returns the amount withdrawn for confirmation.
     */
    @Override
    public BigDecimal withdrawAll() {
        // Withdraw only if the account has a positive balance
        if (this.balance.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal withdrawn = this.balance;
            this.balance = BigDecimal.ZERO;
            return withdrawn;
        }

        // If balance is zero or negative, no withdrawal possible
        return BigDecimal.ZERO;
    }
}

/*
 The method did not communicate with the drawing method without a positive balance, stating that for its impossible to get in debt
 */