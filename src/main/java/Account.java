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
        if(max_overdrawn.compareTo(BigDecimal.ZERO) <= 0) {
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
        if(!(balance.compareTo(this.max_overdrawn.multiply(new BigDecimal(-1))) <= 0)) {
            this.balance = balance;
        }
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
        if(max_overdrawn.compareTo(BigDecimal.ZERO) <= 0) {
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

    @Override
    public void TransferToAccount(IAccount to_account) {
        to_account.deposit(this.balance);
    }

    @Override
    public BigDecimal withdrawAll() {
        if(this.balance.compareTo(this.max_overdrawn) <= 0) { // This can be read as "if (balance <= max_overdrawn)"
            return withdraw(balance);
        }
        return BigDecimal.ZERO;
    }
}
