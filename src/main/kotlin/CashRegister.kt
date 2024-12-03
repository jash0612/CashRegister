/**
 * The CashRegister class holds the logic for performing transactions.
 *
 * @param change The change that the CashRegister is holding.
 */
class CashRegister(private val change: Change) {
    /**
     * Performs a transaction for a product/products with a certain price and a given amount.
     *
     * @param price The price of the product(s).
     * @param amountPaid The amount paid by the shopper.
     *
     * @return The change for the transaction.
     *
     * @throws TransactionException If the transaction cannot be performed.
     */
    fun performTransaction(productPrices: List<Long>, amountPaid: Change): Change {
        if (productPrices.isEmpty()) throw TransactionException("No products provided.")
        if (productPrices.any { it <= 0 }) throw TransactionException("All product prices must be greater than zero.")

        val totalPrice = productPrices.sum()
        if (amountPaid.total < totalPrice) throw TransactionException("Insufficient payment.")
        else if (amountPaid.total == totalPrice) {
            return try{
                addToCashRegister(amountPaid)
                Change.none()
            } catch (e: Exception) {
                throw TransactionException("Unable to add cash to Register.", e)
            }
        }
        else {
            if (change.isEmpty()) throw TransactionException("Empty cash register. No change available.")
            else {
                val changeToReturnAmount = amountPaid.total - totalPrice

                return try {
                    // Calculate the minimal change
                    val changeToReturn = change.calculateMinimalChange(changeToReturnAmount)
                    deductFromCashRegister(changeToReturn)
                    addToCashRegister(amountPaid)

                    changeToReturn
                } catch (e: IllegalArgumentException) {
                    throw TransactionException("Unable to provide the required change.", e)
                }
            }
        }
    }

    // Add the received payment to the register
    private fun addToCashRegister(amountPaid: Change) {
        amountPaid.getElements().forEach { element ->
            change.add(element, amountPaid.getCount(element))
        }
    }

    // Deduct returned change from the register
    private fun deductFromCashRegister(changeToReturn: Change) {
        changeToReturn.getElements().forEach { element ->
            change.remove(element, changeToReturn.getCount(element))
        }
    }

    override fun toString(): String {
        return "CashRegister(change=$change)"
    }

    class TransactionException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
