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
        fail("Not yet implemented"); //TODO implement
    }

    @Test
    void testTransferToAccount() {

        // Main scenario
        Account myTestAccount1 = new Account(new BigDecimal("10"), "SEK", new BigDecimal("100"));
        Account myTestAccount2 = new Account(new BigDecimal("10"), "SEK", new BigDecimal("100"));

        assertEquals(myTestAccount1.getCurrency(), myTestAccount2.getCurrency());

        BigDecimal previous_amount1 =  myTestAccount1.getBalance();
        BigDecimal previous_amount2 =  myTestAccount2.getBalance();

        myTestAccount1.TransferToAccount(myTestAccount2);

        assertEquals(new BigDecimal(0), myTestAccount1.getBalance());
        assertEquals(previous_amount1.add(previous_amount2), myTestAccount2.getBalance());


        // Currencies without matching
        Account myTestAccount3 = new Account(new BigDecimal("100"), "SEK", new BigDecimal("100"));
        Account myTestAccount4 = new Account(new BigDecimal("100"), "USD", new BigDecimal("100"));

        BigDecimal previous_amount3 =  myTestAccount3.getBalance();
        BigDecimal previous_amount4 =  myTestAccount4.getBalance();

        myTestAccount3.TransferToAccount(myTestAccount4);

        assertEquals(previous_amount3, myTestAccount3.getBalance());
        assertEquals(previous_amount4, myTestAccount4.getBalance());

        // Transfering without positive balance
        Account myTestAccount5 = new Account(new BigDecimal("-200"), "SEK", new BigDecimal("100"));
        Account myTestAccount6 = new Account(new BigDecimal("200"), "SEK", new BigDecimal("100"));

        BigDecimal previous_amount5 =  myTestAccount5.getBalance();
        BigDecimal previous_amount6 =  myTestAccount6.getBalance();

        myTestAccount5.TransferToAccount(myTestAccount6);

        assertEquals(previous_amount5, myTestAccount5.getBalance());
        assertEquals(previous_amount6, myTestAccount6.getBalance());
    }

    @Test
    void testWithdrawAll() {

        // Main scenario
        Account myTestAccount1 = new Account(new BigDecimal("10"), "SEK", new BigDecimal("100"));

        myTestAccount1.withdrawAll();

        assertEquals(new BigDecimal("0"), myTestAccount1.getBalance());


        // Negative balance
        Account myTestAccount2 = new Account(new BigDecimal("-10"), "SEK", new BigDecimal("100"));

        myTestAccount2.withdrawAll();

        assertEquals(new BigDecimal("-10"), myTestAccount2.getBalance());

        // 0 withdraw
        Account myTestAccount3 = new Account(new BigDecimal("150"), "SEK", new BigDecimal("0"));

        myTestAccount3.withdrawAll();

        assertEquals(new BigDecimal("150"), myTestAccount3.getBalance());

    }
}
