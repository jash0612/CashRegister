import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import kotlin.test.assertFailsWith

class CashRegisterTest {
    @Test
    fun `should perform transaction for single product with exact payment`() {
        val initialChange = Change()
            .add(Bill.FIVE_HUNDRED_EURO, 2)
        val cashRegister = CashRegister(initialChange)

        val productPrice = listOf(500_00L) // Single product price
        val amountPaid = Change().add(Bill.FIVE_HUNDRED_EURO, 1)

        val changeReturned = cashRegister.performTransaction(productPrice, amountPaid)

        assertEquals(Change.none(), changeReturned) // No change expected
        assertEquals(3, initialChange.getCount(Bill.FIVE_HUNDRED_EURO)) // Payment added to cash register
    }

    @Test
    fun `should perform transaction for multiple products with exact payment`() {
        val initialChange = Change()
            .add(Bill.FIVE_HUNDRED_EURO, 2)
        val cashRegister = CashRegister(initialChange)

        val productPrices = listOf(500_00L,1_00L) // Multiple product prices
        val amountPaid = Change().add(Bill.FIVE_HUNDRED_EURO, 1).add(Coin.ONE_EURO,1)

        val changeReturned = cashRegister.performTransaction(productPrices, amountPaid)

        assertEquals(Change.none(), changeReturned) // No change expected
        assertEquals(3, initialChange.getCount(Bill.FIVE_HUNDRED_EURO)) // Payment added to cash register
        assertEquals(1, initialChange.getCount(Coin.ONE_EURO)) // Payment added to cash register
    }

    @Test
    fun `should perform transaction for single product with change required`() {
        val initialChange = Change()
            .add(Bill.ONE_HUNDRED_EURO, 2)
            .add(Coin.FIFTY_CENT,1)
            .add(Coin.TEN_CENT,5)
            .add(Coin.TWO_CENT,5)
            .add(Coin.FIVE_CENT,2)
        val cashRegister = CashRegister(initialChange)

        val productPrice = listOf(400_00L)
        val amountPaid = Change().add(Bill.FIVE_HUNDRED_EURO, 1)

        val changeReturned = cashRegister.performTransaction(productPrice, amountPaid)
        val changeExpected = Change().add(Bill.ONE_HUNDRED_EURO,1)

        assertEquals(changeExpected, changeReturned)
        assertEquals(1, initialChange.getCount(Bill.ONE_HUNDRED_EURO)) // Payment deducted from cash register
        assertEquals(1, initialChange.getCount(Bill.FIVE_HUNDRED_EURO)) // Payment added to cash register
    }

    @Test
    fun `should perform transaction for multiple products with change required`() {
        val initialChange = Change()
            .add(Bill.ONE_HUNDRED_EURO, 2)
            .add(Coin.TWO_EURO,2)
            .add(Coin.ONE_EURO,1)
            .add(Coin.FIFTY_CENT,1)
            .add(Coin.TEN_CENT,5)
            .add(Coin.TWO_CENT,5)
            .add(Coin.FIVE_CENT,2)
        val cashRegister = CashRegister(initialChange)

        val productPrice = listOf(13_25L,32_75L,50_25L)
        val amountPaid = Change().add(Bill.ONE_HUNDRED_EURO, 1)

        val changeReturned = cashRegister.performTransaction(productPrice, amountPaid)
        val changeExpected = Change()
            .add(Coin.TWO_EURO,1)
            .add(Coin.ONE_EURO,1)
            .add(Coin.FIFTY_CENT,1)
            .add(Coin.TEN_CENT,2)
            .add(Coin.FIVE_CENT,1)

        assertEquals(changeExpected, changeReturned)
        assertEquals(1, initialChange.getCount(Coin.TWO_EURO)) // Payment deducted from cash register
        assertEquals(0, initialChange.getCount(Coin.ONE_EURO)) // Payment deducted from cash register
        assertEquals(0, initialChange.getCount(Coin.FIFTY_CENT)) // Payment deducted from cash register
        assertEquals(3, initialChange.getCount(Coin.TEN_CENT)) // Payment deducted from cash register
        assertEquals(1, initialChange.getCount(Coin.FIVE_CENT)) // Payment deducted from cash register
        assertEquals(3, initialChange.getCount(Bill.ONE_HUNDRED_EURO)) // Payment added to cash register
    }

    @Test
    fun `no products provided`() {
        val cashRegister = CashRegister(Change())

        val exception = assertThrows<CashRegister.TransactionException> {
            cashRegister.performTransaction(emptyList(),Change())
        }

        assertEquals("No products provided.",exception.message)
    }

    @Test
    fun `zero or negative prices provided`() {
        val initialChange = Change()
            .add(Bill.FIVE_HUNDRED_EURO, 2)
        val cashRegister = CashRegister(initialChange)

        val productPrices = listOf(0L,-1_00L)

        val exception = assertThrows<CashRegister.TransactionException> {
            cashRegister.performTransaction(productPrices,Change())
        }

        assertEquals("All product prices must be greater than zero.",exception.message)
    }

    @Test
    fun `insufficient payment`() {
        val initialChange = Change()
            .add(Bill.FIFTY_EURO, 1)
        val cashRegister = CashRegister(initialChange)

        val productPrice = listOf(100_00L) // Price 100 €
        val amountPaid = Change().add(Coin.ONE_EURO, 1) // Paid 1.00 €

        val exception = assertThrows<CashRegister.TransactionException> {
            cashRegister.performTransaction(productPrice, amountPaid)
        }

        assertEquals("Insufficient payment.", exception.message)
    }

    @Test
    fun `empty cash register, no change available`() {
        val cashRegister = CashRegister(Change.none())
        val productPrices = listOf(10_00L) // Total: 10.00
        val amountPaid = Change().add(Bill.TWENTY_EURO, 1) // Payment: 20.00

        val exception = assertThrows<CashRegister.TransactionException> {
            cashRegister.performTransaction(productPrices, amountPaid)
        }

        assertEquals("Empty cash register. No change available.", exception.message)
    }
}
