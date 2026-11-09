# VendingMachine Coverage Report

## Initial and Final Coverage

I ran the complete JUnit5 tests using the option to run tests with coverage and this is what I got as a result:

| Source File | Initial Statement Coverage | Initial Branch Coverage | Final Statement Coverage | Final Branch Coverage |
|---|---|---|---|---|
| VendingMachine.java | 100% | 100% | 100% | 100% |
| VendingMachineItem.java | 100% | 100% | 100% | 100% |

## Coverage-Guided Test Improvement

The initial VendingMachine Project 2 tests I had,  already achieved 100% statement and
branch coverage for both VendingMachine.java and VendingMachineItem.java and when I inspected the
test coverage view and checked if there were any lines not covered, I found nothing negative. All the statements
were covered in these files, none were left uncovered for which I decided I did not need to add any more tests to improve the coverage as it was already 100%.


## Condition-Coverage Analysis

I selected this compound Boolean decision in makePurchase():
(item != null) && (this.balance >= item.getPrice())

The individual conditions are:

- Condition 1: item != null — determines if the slot that was chosen actually 
  has items or is empty.
- Condition 2: this.balance >= item.getPrice() — checks if the current balance is more than or equal to the 
   price of the item.

| Test | Condition 1 | Condition 2 | Decision |
|---|---|---|---|
| testMakePurchase() | True | True: $3.00 >= $1.25 | True |
| testMakePurchaseWithInsufficientFunds() | True | False: $1.00 < $1.25 | False |
| testMakePurchaseFromEmptySlot() | False | False because empty slot | False |
| testMakePurchaseWithAbovePrice() | True | True: $1.26 >= $1.25 | True |

Condition 1 evaluates to false in the empty-slot test and true in the
other three tests. Condition 2 evaluates to false in the insufficient-funds
test and true in the successful-purchase tests. Therefore, each individual
condition evaluates to both true and false across these tests.

## Test-Sensitivity Experiment

### Injected Fault

I temporarily injected a fault into the if statement line of VendingMachine.java method makePurchase()
I changed the operator from >= to <. Thi sparticular fault would have allowed successful purchase even if the balance was below the price of the item and did not meet the required more or equal price.

Original decision:

```java
if ((item != null) && (this.balance >= item.getPrice())) {
```

Fault injected decision:

```java
if ((item != null) && (this.balance < item.getPrice())) { // INJECTED FAULT FOR COVERAGE EXPERIMENT
```

### Observed Failures

| Test | Expected Result | Actual Result |
|---|---|---|
| testMakePurchase() | Purchase returns true | Returned false |
| testMakePurchaseWithInsufficientFunds() | Purchase returns false | Returned true |
| testMakePurchaseWithAbovePrice() | Purchase returns true | Returned false |
| testReturnChange() | Returns $1.75 after the purchase | Returned $3.00 |

All six cases in testMakePurchaseWithDifferentAmounts() also failed:

| Starting Balance | Item Price | Expected Purchase Result | Actual Purchase Result |
|---|---|---|---|
| $0.00 | $1.25 | false | true |
| $1.00 | $1.25 | false | true |
| $1.24 | $1.25 | false | true |
| $1.25 | $1.25 | true | false |
| $1.26 | $1.25 | true | false |
| $3.00 | $1.25 | true | false |

The JUnit failure messages included:

- Expected [true] but was [false]
- Expected [false] but was [true]
- Expected [1.75] but was [3.0]


### Restoration and Final Result

After recording the failures, I restored the >= comparison and removed
the injected-fault comment. I reran the complete suite with coverage,
and all 32 test executions passed.


