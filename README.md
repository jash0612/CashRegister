## Problem Overview

The goal is to implement a cash register.

**Criteria:**

- The ``CashRegister`` gets initialized with some ``Change``.
- When performing a transaction, it either returns a ``Change`` object or fails with a ``TransactionException``.
- The ``CashRegister`` keeps track of the ``Change`` that's in it.

**Bonus points:**

- The cash register returns the minimal amount of change (i.e., the minimal amount of coins/bills)

## Design and Implementation Approach

### **Core Functionalities**

1. **Cash Register Initialization**:
    - A `CashRegister` object holds a `Change` object that represents the denominations and their counts.
    - It allows dynamic updates through `add` and `remove` methods in the `Change` class.
2. **Transaction Logic**:
    - **Price Calculation**: Supports multiple products by summing up their prices.
    - **Validation**: Ensures sufficient payment and handles invalid input scenarios.
    - **Change Calculation**: Uses the `calculateMinimalChange` method to return the minimal denominations as change.
3. **Minimal Change Algorithm**:
    - Iterates through available denominations from highest to lowest value.
    - Calculates the maximum number of each denomination that can be used for the change.
    - Fails gracefully when exact change is impossible.
4. **State Tracking**:
    - Updates the `CashRegister` state after each successful transaction.
    - Prevents invalid state changes (e.g., removing more denominations than available).
5. **Error Handling**:
    - Custom `TransactionException` is thrown for insufficient payment or invalid inputs.
    - Invalid operations in `Change` (e.g., negative counts) are caught using validation logic.

## Key Classes and Functions

1. **`Change`**:
    - Represents a collection of denominations (`MonetaryElement`) with counts.
    - Supports adding/removing denominations and calculates the total value dynamically.
    - Calculates minimal change.
2. **`CashRegister`**:
    - Core transaction logic:
        - Validates payment.
        - Updates the cash register state.
    - Handles multiple product transactions.
3. **`MonetaryElement`, `Bill`, and `Coin`**:
    - Represents monetary denominations with fixed values.
    - Enables extensibility for other currencies or denominations.
4. **`calculateMinimalChange`**:
    - A utility function within `Change` to compute minimal change using a greedy algorithm.
5. **Main Function**:
    - Interactive console application to simulate transactions.
    - Dynamically updates and prints the `CashRegister` state.

## Test Cases

### **Tools and Frameworks**:

- Kotlin's **JUnit** for testing.
- Assertions from `kotlin.test` for validating outcomes.

### **Test Plan**

1. **Initialization Tests**:
    - Test initialization of `CashRegister` with various `Change` objects.
    - Verify the initial state of the cash register matches the provided denominations.
2. **Transaction Tests**:
    - **Successful Transactions**:
        - Single product with exact payment.
        - Single product with change required.
        - Multiple products with exact payment.
        - Multiple products with change required.
    - **Edge Cases**:
        - No products provided.
        - Products with price 0 or negative values.
        - Payment insufficient for the total price.
        - Payment resulting in complex change combinations.
    - **Exceptions**:
        - Throw `TransactionException` for invalid inputs or insufficient change.
3. **Minimal Change Tests**:
    - Verify minimal change calculation logic for complex scenarios.
    - Test situations where exact change is impossible, ensuring the exception is thrown.
4. **State Tests**:
    - Ensure `CashRegister` state updates correctly after successful transactions.

## Conclusion

This implementation delivers a robust and extensible cash register system. Its modular architecture ensures both testability and maintainability.
