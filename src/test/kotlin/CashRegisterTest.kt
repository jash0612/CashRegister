import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

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
}
