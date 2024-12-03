import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ChangeTest {
    @Test
    fun testEquals() {
        val expected = Change()
            .add(Coin.FIVE_CENT, 3)
            .add(Coin.TWO_CENT, 1)
            .add(Bill.FIFTY_EURO, 2)
        val actual = Change()
            .add(Bill.FIFTY_EURO, 2)
            .add(Coin.FIVE_CENT, 3)
            .add(Coin.TWO_CENT, 1)
        assertEquals(expected, actual)
    }

    @Test
    fun testElementsDiffer() {
        val expected = Change()
            .add(Coin.TWO_EURO, 4)
            .add(Bill.TEN_EURO, 1)
            .add(Coin.FIFTY_CENT, 3)
            .add(Coin.TWENTY_CENT, 2)
        val actual = Change()
            .add(Coin.TWO_EURO, 4)
            .add(Coin.TEN_CENT, 1)
            .add(Coin.FIFTY_CENT, 3)
            .add(Coin.TWENTY_CENT, 2)
        assertNotEquals(expected, actual)
    }

    @Test
    fun testCountsDiffer() {
        val expected = Change()
            .add(Coin.TWO_EURO, 4)
            .add(Bill.ONE_HUNDRED_EURO, 1)
            .add(Coin.FIFTY_CENT, 3)
            .add(Coin.TWENTY_CENT, 2)
        val actual = Change()
            .add(Coin.TWO_EURO, 3)
            .add(Coin.TWENTY_CENT, 1)
            .add(Coin.FIFTY_CENT, 2)
            .add(Bill.ONE_HUNDRED_EURO, 1)
        assertNotEquals(expected, actual)
    }

    @Test
    fun `should initialize with correct change`() {
        val initialChange = Change()
            .add(Bill.FIVE_HUNDRED_EURO, 1)
            .add(Coin.ONE_EURO, 5)

        assertEquals(setOf(Bill.FIVE_HUNDRED_EURO, Coin.ONE_EURO), initialChange.getElements())
        assertEquals(5, initialChange.getCount(Coin.ONE_EURO))
    }

    @Test
    fun `should return minimal change for complex amounts`() {
        val initialChange = Change()
            .add(Coin.ONE_EURO, 5)
            .add(Coin.FIFTY_CENT, 10)
            .add(Coin.TWENTY_CENT, 5)
            .add(Coin.TEN_CENT, 3)
        val cashRegister = CashRegister(initialChange)

        val productPrice = listOf(1_30L) // Price 1.30 €
        val amountPaid = Change()
            .add(Coin.TWO_EURO, 1) // Paid 2.00 €

        val changeReturned = cashRegister.performTransaction(productPrice, amountPaid)

        assertEquals(1, changeReturned.getCount(Coin.FIFTY_CENT))
        assertEquals(1, changeReturned.getCount(Coin.TWENTY_CENT))
        assertEquals(5, initialChange.getCount(Coin.ONE_EURO)) // Updated cash register state
        assertEquals(1, initialChange.getCount(Coin.TWO_EURO)) // Updated cash register state
        assertEquals(9, initialChange.getCount(Coin.FIFTY_CENT)) // Updated cash register state
        assertEquals(4, initialChange.getCount(Coin.TWENTY_CENT)) // Updated cash register state
        assertEquals(3, initialChange.getCount(Coin.TEN_CENT)) // Updated cash register state
    }

    @Test
    fun `change not possible with available denominations`() {
        val cashRegister = CashRegister(Change().add(Coin.TWO_EURO, 2)) // 4.00 in coins
        val productPrices = listOf(1_00L) // Total: 1.00
        val amountPaid = Change().add(Bill.TWENTY_EURO, 1) // Payment: 20.00

        val exception = assertThrows<CashRegister.TransactionException> {
            cashRegister.performTransaction(productPrices, amountPaid)
        }

        assertEquals("Insufficient change available to provide the minimal change.",exception.message)
    }
}
