import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


class AccountTest {

    @Test
    void testGetMaxOverdrawn() {
        Account myTestAccount = new Account(BigDecimal.ZERO, "SEK", BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, myTestAccount.getMaxOverdrawn());

        Account myTestAccount2 = new Account(BigDecimal.ZERO, "SEK", new BigDecimal(-1));
        assertEquals(BigDecimal.ZERO, myTestAccount2.getMaxOverdrawn()); //max_overdrawn must be non-negative

        Account myTestAccount3 = new Account(BigDecimal.ZERO, "SEK", new BigDecimal(1000));
        assertEquals(new BigDecimal(1000), myTestAccount3.getMaxOverdrawn());
    }

    @Test
    void testSetMaxOverdrawn() {
        Account myTestAccount = new Account(BigDecimal.ZERO, "SEK", BigDecimal.ZERO);

        myTestAccount.setMaxOverdrawn(new BigDecimal(-1));
        assertEquals(BigDecimal.ZERO, myTestAccount.getMaxOverdrawn()); //max_overdrawn must be non-negative

        myTestAccount.setMaxOverdrawn(new BigDecimal(100));
        assertEquals(new BigDecimal(100), myTestAccount.getMaxOverdrawn()); //max_overdrawn must be non-negative
    }

    @Test
    void testGetCurrency() {
        Account myTestAccount = new Account(BigDecimal.ZERO,  "SEK", BigDecimal.ZERO);
        assertEquals("SEK", myTestAccount.getCurrency());

        myTestAccount = new Account(BigDecimal.ZERO,  "EUR", BigDecimal.ZERO);
        assertEquals("EUR", myTestAccount.getCurrency());

        myTestAccount = new Account(BigDecimal.ZERO,  "USD", BigDecimal.ZERO);
        assertEquals("USD", myTestAccount.getCurrency());
    }

    @Test
    void testSetCurrency() {
        Account myTestAccount = new Account(BigDecimal.ZERO, "SEK", BigDecimal.ZERO);
        myTestAccount.setCurrency("EUR");
        assertEquals("EUR", myTestAccount.getCurrency());

        myTestAccount.setCurrency("SEK");
        assertEquals("SEK", myTestAccount.getCurrency());
    }

    @Test
    void testGetBalance() {
        Account myTestAccount = new Account(BigDecimal.ZERO, "SEK", BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, myTestAccount.getBalance());

        myTestAccount = new Account(new BigDecimal(100), "SEK", BigDecimal.ZERO);
        assertEquals(new BigDecimal(100), myTestAccount.getBalance());
    }

    @Test
    void testSetBalance() {
        Account myTestAccount = new Account(BigDecimal.ZERO, "SEK", BigDecimal.ONE);

        //should not be allowed to set balance to lower that -1 * maxOverdrawn
        myTestAccount.setBalance(new BigDecimal(-2));
        assertEquals(BigDecimal.ZERO, myTestAccount.getBalance());

        myTestAccount.setBalance(new BigDecimal(42));
        assertEquals(new BigDecimal(42), myTestAccount.getBalance());
    }

    @Test
    void testWithdraw() {
        /*
         * Expected behavior: Withdrawals within balance + maxOverdrawn should succeed
         *                     and update the balance correctly.
         * Provided skeleton behavior: Original withdraw() did not update the balance
         *                             and did not respect maxOverdrawn.
         * Reason for input:
         *     - Test withdrawing 120 from 100 with maxOverdrawn 50 (edge case within limit)
         *     - Test withdrawing more than allowed (should fail and balance remains)
         * Fix applied:
         *     - Updated withdraw() to check maxOverdrawn limit
         *     - Updated withdraw() to set this.balance only if allowed
         */

        // Case 1: Withdraw within allowed limit
        Account myTestAccount = new Account(new BigDecimal("100"), "SEK", new BigDecimal("50"));
        BigDecimal newBalance = myTestAccount.withdraw(new BigDecimal("120"));
        assertEquals(new BigDecimal("-20"), newBalance);
        assertEquals(new BigDecimal("-20"), myTestAccount.getBalance());

        // Case 2: Withdraw exceeding allowed limit (should fail)
        BigDecimal attempt = myTestAccount.withdraw(new BigDecimal("100"));
        assertEquals(new BigDecimal("-20"), attempt); // Balance unchanged
        assertEquals(new BigDecimal("-20"), myTestAccount.getBalance());

        // Case 3: Withdraw exact maximum allowed
        Account edgeAccount = new Account(new BigDecimal("100"), "SEK", new BigDecimal("50"));
        BigDecimal exactMax = edgeAccount.withdraw(new BigDecimal("150"));
        assertEquals(new BigDecimal("-50"), exactMax);
        assertEquals(new BigDecimal("-50"), edgeAccount.getBalance());
    }

    @Test
    void testDeposit() {
        /*
         * Expected behavior: Deposit adds the amount to the account balance.
         *                     Negative deposits are ignored.
         * Provided skeleton behavior: Original deposit() did not update balance.
         * Reason for input:
         *     - Deposit 50 into 100 balance -> expect 150
         *     - Deposit 0 -> expect no change
         *     - Deposit negative (-10) -> expect no change
         * Fix applied:
         *     - Updated deposit() to modify balance and prevent negative deposits
         */

        Account myTestAccount = new Account(new BigDecimal("100"), "SEK", new BigDecimal("50"));

        // Case 1: Normal deposit
        BigDecimal newBalance = myTestAccount.deposit(new BigDecimal("50"));
        assertEquals(new BigDecimal("150"), newBalance);
        assertEquals(new BigDecimal("150"), myTestAccount.getBalance());

        // Case 2: Deposit zero
        newBalance = myTestAccount.deposit(BigDecimal.ZERO);
        assertEquals(new BigDecimal("150"), newBalance);
        assertEquals(new BigDecimal("150"), myTestAccount.getBalance());

        // Case 3: Deposit negative -> balance should not change
        newBalance = myTestAccount.deposit(new BigDecimal("-10"));
        assertEquals(new BigDecimal("150"), newBalance);
        assertEquals(new BigDecimal("150"), myTestAccount.getBalance());
    }

    @Test
    void testConvertToCurrency() {
        /*
         * Expected behavior:
         *   - convertToCurrency() updates balance and currency correctly.
         *   - No return value (void method).
         *
         * Provided skeleton behavior:
         *   - Originally did not update balance or currency.
         *
         * Reason for input:
         *   - Case 1: Conversion to same currency (rate = 1)
         *   - Case 2: Conversion from USD to EUR (rate = 0.92)
         *   - Case 3: Invalid rate (0.0) — should not change balance or currency
         *
         * Fix applied:
         *   - Added logic to update both balance and currency.
         *   - Handled invalid rate  (no update performed).
         */

        // Case 1: Normal conversion, same currency
        Account acc1 = new Account(new BigDecimal("100"), "SEK", new BigDecimal("50"));
        acc1.convertToCurrency("SEK", 1);
        assertEquals("SEK", acc1.getCurrency());
        assertEquals(0, acc1.getBalance().compareTo(new BigDecimal("100")));

        // Case 2: Convert USD → EUR,  rate 0.92
        Account acc2 = new Account(new BigDecimal("100"), "USD", new BigDecimal("100"));
        acc2.convertToCurrency("EUR", 0.92);
        assertEquals("EUR", acc2.getCurrency());
        assertEquals(0, acc2.getBalance().compareTo(new BigDecimal("92.00")));

        // Case 3: Invalid rate (0.0) — should not change values
        Account acc3 = new Account(new BigDecimal("100"), "USD", new BigDecimal("100"));
        acc3.convertToCurrency("EUR", 0.0); // invalid
        assertEquals("USD", acc3.getCurrency());
        assertEquals(0, acc3.getBalance().compareTo(new BigDecimal("100")));
    }
    
    @Test
    void testTransferToAccount() {
        fail("Not yet implemented"); //TODO implement
    }

    @Test
    void testWithdrawAll() {
        fail("Not yet implemented"); //TODO implement
    }
}
