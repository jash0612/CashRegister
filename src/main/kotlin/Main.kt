fun main() {
    val cashRegister = CashRegister(
        Change()
            .add(Bill.FIVE_HUNDRED_EURO, 2)
            .add(Bill.ONE_HUNDRED_EURO,3)
            .add(Bill.FIFTY_EURO,5)
            .add(Bill.TWENTY_EURO,7)
            .add(Bill.TEN_EURO,6)
            .add(Coin.ONE_EURO, 10)
            .add(Coin.FIFTY_CENT, 20)
            .add(Coin.TWENTY_CENT,1)
            .add(Coin.TEN_CENT,2)
            .add(Coin.FIVE_CENT,5)
            .add(Coin.TWO_CENT,10)
            .add(Coin.ONE_CENT,15)

    )

    println("Welcome to the Cash Register System!")
    println("Initial cash register state: $cashRegister")
    println("Enter product prices (comma-separated) or 0 to exit:")

    while (true) {
        print("\nProduct prices (comma-separated): ")
        val input = readlnOrNull() ?: break
        if (input.trim() == "0") {
            println("Exiting. Thank you!")
            break
        }

        try {
            // Parse product prices as minor units
            val productPrices = input.split(",").map {
                val trimmedInput = it.trim()
                if (!trimmedInput.matches(Regex("\\d+(\\.\\d{1,2})?"))) {
                    throw IllegalArgumentException("Invalid price format.")
                }
                (trimmedInput.toDouble() * 100).toLong()
            }

            print("Enter payment (denominations: e.g., '500_00x1 (500 Euros - 1),20x2 (20 cents - 2): ")
            val paymentInput = readlnOrNull() ?: break
            val amountPaid = Change()
            paymentInput.split(",").forEach { it ->
                var (denomination, count) = it.split("x").map { str -> str.trim() }
                val countInt = count.toInt()
                denomination = denomination.replace("_","")
                when {
                    denomination.endsWith("00") -> {
                        val bill =
                            Bill.values().firstOrNull { it.minorValue == denomination.toInt() }
                        bill?.let { amountPaid.add(it, countInt) }
                    }

                    else -> {
                        val coin =
                            Coin.values().firstOrNull { it.minorValue == denomination.toInt() }
                        coin?.let { amountPaid.add(it, countInt) }
                    }
                }
            }

            val changeReturned = cashRegister.performTransaction(productPrices, amountPaid)
            println("Transaction successful! Change returned: $changeReturned")
            println("Updated cash register state: $cashRegister")

        } catch (e: CashRegister.TransactionException) {
            println("Transaction failed: ${e.message}")
        } catch (e: Exception) {
            println("Invalid input. Please try again.")
        }
    }
}