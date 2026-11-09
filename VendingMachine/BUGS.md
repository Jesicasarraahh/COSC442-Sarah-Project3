# Bugs Found and Corrected

## Bug 1: Vending machine constructor uses an invalid array index

### Observed failure
The test failed when it tried to create a new VendingMachine object and tested the testVendingMachine() method. It gave an error message that said: java.lang.ArrayIndexOutOfBoundsException: Index 4 out of bounds for length 4. It failed before we could check the balance or slots.

### Test that exposed the fault
testVendingMachine() in VendingMachineTest.java.

### Source-code fault
The vendingMachine() loop used i <= NUM_SLOTS and because the NUM_SLOTS is 4 (A, B, C, D) the loop allowed i to reach 4. However, an array containing four elements only has valid indexes 0, 1, 2, and 3.

### How I diagnosed the fault
I placed a breakpoint on itemArray[i] = null inside the constructor and debugged the test. I checked i and this.itemArray.length in the Debug Console and it showed the array length was 4 and then I kept pressing continue through the loop and saw the index i increase and go up to 4 which showed the loop was accessing itemArray[4] which is outside the array index.

### Correction
I changed the loop condition from:

i <= NUM_SLOTS

to:

i < NUM_SLOTS

The loop now only accesses indexes 0 through 3.

### Verification
I reran testVendingMachine() after the correction, and it passed. 

## Bug 2: Valid money amounts below $1 were rejected

### Observed failure
testInsertMoney() failed when inserting $0.50. The method threw a VendingMachineException before the balance assertion even ran. 

### Test that exposed the fault
testInsertMoney() in VendingMachineTest.java.

### Source-code fault
The insertMoney() method had amount < 1 and that threw an exception because it rejected any amount below $1; however positive amounts below $1 are valid according to the Javadocs.

### How I diagnosed the fault
I placed a breakpoint on the condition inside insertMoney() and debugged testInsertMoney().

The Debug Console showed:
- amount was 0.50.
- amount < 1 was true.
- amount < 0 was false.

This showed that the condition rejected a valid amount because it compared the amount with 1 instead of 0.

### Correction
I changed the condition from amount < 1 to amount < 0. 

### Verification
I reran testInsertMoney(), and it passed. 

## Test Sensitivity Experiment

### Deliberate fault introduced
I  changed getPrice() in VendingMachineItem.java from return price to return price + 5.67 which made the returned price 5.67 higher than whatever is stored.

### Tests that detected the fault
- testGetPrice()
- testConstructorWithZeroPrice()
- testMakePurchase()
- testMakePurchaseWithAbovePrice()
- testReturnChange()
- testMakePurchaseWithDifferentAmounts(): cases with $1.25, $1.26, and $3.00

### Relevant JUnit failure messages
- testGetPrice(): Expected [3.0] but was [8.67]
- testConstructorWithZeroPrice(): Expected [0.0] but was [5.67]
- Successful purchase cases: Expected [true] but was [false]
- testReturnChange(): Expected [1.75] but was [3.0]

### Why the tests detected the fault
The price tests compared the returned price with the price supplied when creating the item. Adding $5.67 caused those values to differ.The make purchase tests detected the fault because the $1.25 popcorn reported a price of $6.92. Balances that should have covered the original price were no longer sufficient, so the purchases returned false.The return change test expected $1.75 after purchasing popcorn with $3.00. Since the purchase failed, the full $3.00 remained and was returned instead.

### The Correction I made
I have removed the injected fault return price + 5.67 and corrected it back to return price. Then i ran all three tests together again and they all passed.
