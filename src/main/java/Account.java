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
        if (!(balance.compareTo(this.max_overdrawn.multiply(new BigDecimal(-1))) <= 0)) {
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
        this.currency = currencyCode;
        this.balance.multiply(new BigDecimal(rate));
    }

    @Override
    public void TransferToAccount(IAccount to_account) {

        // I make out of the interface the object to be able to get the currency
        if (to_account instanceof Account) {
            Account targetAccount = (Account) to_account;

            // Only transfer if my balance is positive
            if (this.balance.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Transfer failed: Negative or Zero balance");
                return;
            }

            // Only transfer if currencies match
            if (this.currency.equals(targetAccount.getCurrency())) {
                // Transfer full balance
                targetAccount.deposit(this.balance);
                // Set sender's balance to zero
                this.balance = BigDecimal.ZERO;

            } else  {
                System.out.println("Transfer failed: currencies do not match.");
                return;
            }
        }
    }

    /*
    Initially the method did not update the sender's balance, leading to errors, eve though the reciever was being updated
     */



    @Override
    public BigDecimal withdrawAll() {

        // I can only withdraw if I have positive balance
        if (this.balance.compareTo(BigDecimal.ZERO) < 0) {
            return this.balance;
        }

        // If I can not be in debt in my account ill never be able to withdraw all
        if (this.max_overdrawn.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Transfer failed: No withdraw");
            return this.balance;
        }

        this.balance = BigDecimal.ZERO;
        return BigDecimal.ZERO;
    }
}

/*
 The method did not xxx with drawing without a positive balance, stating that fo us is imposible to get in debt
 */

