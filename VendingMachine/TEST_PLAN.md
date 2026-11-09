| Method / Behavior | Valid Case(s) | Exception / Invalid Case(s) | Boundary Case(s) | Oracle / Expected Result | Related JUnit Test(s) |
| --- | --- | --- | --- | --- | --- |
| VendingMachine() | Create a new vending machine and check if the slots A-D are empty and if not get the initial balance | Nothing because no arguments are being taken | Nothing because there are no input values. | The javadoc says that the four codes A-D start with null and zero balance, so getBalance() should return $0.0 and getItem() should return null. | testVendingMachine() | |
| addItem(item, code) | Adding an item “drink” to the vending machine, maybe at slot A then get the item from A to see if the same object was stored. | If the slot A already has an item adding another item will throw a VendingMachine Exception. The test should also fail if the slot index is out of the boundary of A-D. | If a new drink to A is added when it is empty it will succeed and then add another item immediately after, it should throw a VendingMachineException because A now contains its maximum of one item. | The method’s javadoc says that the slot needs to be empty for us to add an item if it is already filled we will get a VendingMachineException and also if we add item to an invalid slot that is out of the boundary. | testAddItem(), testAddItemToFilledSlot, testAddItemToInvalidSlot | |
| removeItem(code) | Add a new item “Cup Noodles” to slot D and call removeItem(“D”) and check if the item has been removed. | As D is already an empty slot here due to removing the item, if we try removing from D again it will show VendingMachine Exception, same for if we try to remove from slot G. | After removing Cup Noodles item in D try to remove it again it should throw VendingMachineException because the slot now contains zero items. | The javadoc says that if the code slot is not empty the item we want to remove will be removed, we get a vendingMachineException if the slot is invalid and out of boundary or already empty because there is nothing left to remove. | testRemoveItem(), testRemoveItemFromInvalidSlot, testRemoveItemFromEmptySlot | |
| insertMoney(amount) | Start with inserting $0.50 now the balance should be $0.50 then add $2.50 new balance should be $3.0. | If we insert a negative amount it will throw VendingMachine Exception | Insert −0.01, 0.00 and 0.02 in separate new machines and the negative will throw an exception and the zero will be accepted and 0.02 will change the balance. | The precondition allows amounts greater than or equal to zero. The postcondition says the amount is added to the previous balance. Therefore, inserting $0.50 and then $2.50 should produce $3.00, because 0.50 + 2.50 = 3.00. A negative amount should throw VendingMachineException. In the boundary cases, zero should leave the balance unchanged, and $0.02 should increase it by $0.02. | testInsertMoney(), testInsertNegativeAmount()  | |
| getItem(code) | Add a new item “MandMs” to empty slot B Retrieve the item from slot B. | Try calling getItem(“H”) and it will throw expect | Calling getItem(“B”) will give null before adding the “MandMs” and call it again after adding it and it will return “MandMs” | The method’s specification says it returns the item occupying the requested slot. Therefore, B should return null before adding M&Ms and the same M&Ms object afterward. The Javadoc says an invalid code causes VendingMachineException, so H should throw that exception. | testGetItem(), testGetItemFromInvalidSlot(), testGetItemFromEmptySlot()  | |
| getBalance() | Insert $3.0 and call getBalance() and it should return $3.0, the balance should remain the same amount as before the function was called. | Nothing because this method did not take any arguments | Creating a new machine call getBalance it should return 0.0$ but then insert $3.0 and call it again it will return the balance $3.0. | The method’s postcondition says the balance stays unchanged when it is read. A new machine should return $0.00. After inserting $3.00, repeated calls should return $3.00 without reducing it. | testGetBalance() | |
| makePurchase(code) | Add a “Popcorn” item to slot C with a price of $1.25. Insert $3.0 and the new balance should be $1.75, but because the $3.0 was more than the price it should return true to indicate the purchase was made successfully. Then try to do the same but insert $1.0 it should return false because that is less than the price. | If the code inputted is empty like lets say D, it will return false. In a new code if you insert money that is less than the price of the item you want you will get false. | Create new machines each time the price of popcorn is $1.25 but try inserting $1.24, $1.26 for both these cases it will return false and then try inserting $1.25 it will return true. | The specification says a purchase succeeds when an item is present and the balance covers its price. Buying the $1.25 Popcorn with $3.00 should return true, empty C, and leave $1.75, because 3.00 − 1.25 = 1.75. With only $1.00, it should return false without changing the item or balance. Buying from empty D should also return false. At the price boundary, $1.24 should fail, while $1.25 and $1.26 should succeed and leave $0.00 and $0.01, respectively. | testMakePurchase(), testMakePurchaseFromEmptySlot(), testMakePurchaseWithBelowPrice(), testMakePurchaseWithAbovePrice() | |
| returnChange() | The balance after successful purchase should be $1.75 and call returnChange() it should return that amount and change the balance to $0.0. | Nothing because this method did not take any arguments | After successful Popcorn purchase, call returnChange() to receive $1.75. Call it again immediately; it should return $0.00, and the balance should remain zero. | The method’s specification says it returns the remaining balance and resets the balance to zero. Therefore, after the Popcorn purchase, it should return $1.75 and leave $0.00. An immediate second call should return $0.00, because the first call already refunded  all remaining money. | testReturnChange() | |
| getName() | Create an item named “donut” and call getName() and it should return “donut”. | Nothing because this method is not taking arguments. | Nothing | The getter’s postcondition says it returns the item’s actual stored name. Because the item was created with the name "donut", the expected result is exactly "donut". | testGetName() | |
| getPrice() | Set the price of the item “donut” created to $3.0 and call getPrice() it should return $3.0. | If the price is negative the test will fail. | Create donut items with different prices and check it returns the prices when called. | The getter’s postcondition says it returns the item’s actual stored price. Because the donut was created with a price of $3.00, the expected result is 3.00. For other valid prices, the returned value should match the price supplied when creating each item. | testGetPrice() | |


## Parameterized Test: Purchasing with Different Balances

Related JUnit test: testMakePurchaseWithDifferentAmounts()

This test uses @ParameterizedTest and @CsvSource to run the same purchase behavior with six different balances. Each run creates a new vending machine and adds a Popcorn item costing $1.25 to slot C.

| Inserted Amount | Expected Purchase Result | Expected Remaining Balance | Expected Slot C |
| --- | --- | --- | --- |
| $0.00 | false | $0.00 | Still contains the popcorn |
| $1.00 | false | $1.00 | Still contains the popcorn |
| $1.24 | false | $1.24 | Still contains the popcorn |
| $1.25 | true | $0.00 | Empty |
| $1.26 | true | $0.01 | Empty |
| $3.00 | true | $1.75 | Empty |

### Why these values were selected

I chose these specific values to test the values below, above and the actual price. Then 0.00 shows purchase was being tried without putting any money which case this returned false, then $1.0 returns an insufficient balance because that is below the price of the item. The $1.24, $1.25, and $1.26 cases check immediately below, exactly at, and immediately above the $1.25 price boundary. The $3.00 case checks a balance comfortably above the price.

### Oracle / Expected Results

The purchase specification says that a purchase succeeds when the selected slot contains an item and the balance is greater than or equal to the item's price.

For balances below $1.25, makePurchase("C") should return false. The popcorn should remain in slot C, and the balance should remain unchanged.

For balances equal to or above $1.25, makePurchase("C") should return true. Slot C should become empty, and the item's price should be subtracted from the balance.

The expected remaining balances were calculated manually:

- $1.25 - $1.25 = $0.00
- $1.26 - $1.25 = $0.01
- $3.00 - $1.25 = $1.75

These expectations come from the purchase specification and manual calculations, rather than from what the program returned.