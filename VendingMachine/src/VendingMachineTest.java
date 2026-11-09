import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class VendingMachineTest {

    @Test
    void testVendingMachine() {
        VendingMachine vm = new VendingMachine();
        assertEquals(0.0, vm.getBalance(), 0.001);
        assertNull(vm.getItem("A"));
        assertNull(vm.getItem("B"));
        assertNull(vm.getItem("C"));
        assertNull(vm.getItem("D"));

    }

    @Test
    void testAddItem() {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem drink = new VendingMachineItem("drink", 2.00);

        // act
        vm.addItem(drink, "A");

        // assert
        assertSame(drink, vm.getItem("A"));

    }

    @Test
    void testAddItemToFilledSlot() {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem drink = new VendingMachineItem("drink", 2.00);
        VendingMachineItem chips = new VendingMachineItem("chips", 1.50);
        vm.addItem(drink, "A");

        // assert
        assertThrows(VendingMachineException.class, () -> {
            vm.addItem(chips, "A");
        });
        assertSame(drink, vm.getItem("A"));

    }

    @Test
    void testAddItemWithInvalidCode() {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem drink = new VendingMachineItem("drink", 2.00);

        // assert
        assertThrows(VendingMachineException.class, () -> {
            vm.addItem(drink, "G");
        });
    }

    @Test
    void testGetBalance() {
        // arrange
        VendingMachine vm = new VendingMachine();
        vm.insertMoney(3.00);
        // act
        double balance = vm.getBalance();
        // assert
        assertEquals(3.00, balance, 0.001);
        assertEquals(3.00, vm.getBalance(), 0.001);

    }

    @Test
    void testGetItem() {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem chocolate = new VendingMachineItem("MandMs", 1.50);
        vm.addItem(chocolate, "B");
        // act
        VendingMachineItem retrievedItem = vm.getItem("B");

        // assert
        assertSame(chocolate, retrievedItem);
        assertSame(chocolate, vm.getItem("B"));

    }

    @Test
    void testGetItemFromEmptySlot() {
        // arrange
        VendingMachine vm = new VendingMachine();
        // act
        VendingMachineItem item = vm.getItem("B");
        // assert
        assertNull(item);
    }

    @Test
    void testGetItemWithInvalidCode() {
        // arrange
        VendingMachine vm = new VendingMachine();
        // assert
        assertThrows(VendingMachineException.class, () -> {
            vm.getItem("H");
        });
    }

    @Test
    void testInsertMoney() {
        // arrange
        VendingMachine vm = new VendingMachine();
        // act
        vm.insertMoney(0.50);
        // assert
        assertEquals(0.50, vm.getBalance(), 0.001);
        // act
        vm.insertMoney(2.50);
        // assert
        assertEquals(3.00, vm.getBalance(), 0.001);

    }

    @Test
    void testInsertNegativeMoney() {
        // arrange
        VendingMachine vm = new VendingMachine();
        vm.insertMoney(3.00);
        // assert
        assertThrows(VendingMachineException.class, () -> {
            vm.insertMoney(-1.00);
        });
        assertEquals(3.00, vm.getBalance(), 0.001);
    }

    @Test
    void testInsertZeroMoney() {
        // arrange
        VendingMachine vm = new VendingMachine();
        // act
        vm.insertMoney(0.00);
        // assert
        assertEquals(0.00, vm.getBalance(), 0.001);
    }

    @Test
    void testInsertLessThanOneDollarMoney() {
        // arrange
        VendingMachine vm = new VendingMachine();
        // act
        vm.insertMoney(0.02);
        // assert
        assertEquals(0.02, vm.getBalance(), 0.001);
    }

    @Test
    void testMakePurchase() {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem popcorn = new VendingMachineItem("Popcorn", 1.25);
        vm.addItem(popcorn, "C");
        vm.insertMoney(3.00);
        // act
        boolean purchased = vm.makePurchase("C");
        // assert
        assertTrue(purchased);
        assertNull(vm.getItem("C"));
        assertEquals(1.75, vm.getBalance(), 0.001);

    }

    @Test
    void testMakePurchaseWithInsufficientFunds() {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem popcorn = new VendingMachineItem("Popcorn", 1.25);
        vm.addItem(popcorn, "C");
        vm.insertMoney(1.00);
        // act
        boolean purchased = vm.makePurchase("C");
        // assert
        assertFalse(purchased);
        assertSame(popcorn, vm.getItem("C"));
        assertEquals(1.00, vm.getBalance(), 0.001);
    }

    @Test
    void testMakePurchaseFromEmptySlot() {
        // arrange
        VendingMachine vm = new VendingMachine();
        vm.insertMoney(3.00);
        // act
        boolean purchased = vm.makePurchase("D");
        // assert
        assertFalse(purchased);
        assertNull(vm.getItem("D"));
        assertEquals(3.00, vm.getBalance(), 0.001);
    }

    @Test
    void testMakePurchaseWithAbovePrice() {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem popcorn = new VendingMachineItem("Popcorn", 1.25);
        vm.addItem(popcorn, "C");
        vm.insertMoney(1.26);
        // act
        boolean purchased = vm.makePurchase("C");
        // assert
        assertTrue(purchased);
        assertNull(vm.getItem("C"));
        assertEquals(0.01, vm.getBalance(), 0.001);
    }

    @Test
    void testRemoveItem() {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem noodles = new VendingMachineItem("Cup Noodles", 2.50);
        vm.addItem(noodles, "D");

        // act
        VendingMachineItem removedItem = vm.removeItem("D");

        // assert
        assertSame(noodles, removedItem);
        assertNull(vm.getItem("D"));

    }

    @Test
    void testRemoveItemFromEmptySlot() {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem noodles = new VendingMachineItem("Cup Noodles", 2.50);
        vm.addItem(noodles, "D");
        vm.removeItem("D");

        // assert
        assertThrows(VendingMachineException.class, () -> {
            vm.removeItem("D");
        });
        assertNull(vm.getItem("D"));
    }

    @Test
    void testRemoveItemWithInvalidCode() {
        // arrange
        VendingMachine vm = new VendingMachine();

        // assert
        assertThrows(VendingMachineException.class, () -> {
            vm.removeItem("G");
        });
    }

    @Test
    void testReturnChange() {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem popcorn = new VendingMachineItem("Popcorn", 1.25);
        vm.addItem(popcorn, "C");
        vm.insertMoney(3.00);
        vm.makePurchase("C");
        // act
        double change = vm.returnChange();
        // assert
        assertEquals(1.75, change, 0.001);
        assertEquals(0.00, vm.getBalance(), 0.001);
        // requesting change when balance is 0
        assertEquals(0.00, vm.returnChange(), 0.001);
        assertEquals(0.00, vm.getBalance(), 0.001);

    }

    @ParameterizedTest
    @CsvSource({
            "0.00, false, 0.00",
            "1.00, false, 1.00",
            "1.24, false, 1.24",
            "1.25, true, 0.00",
            "1.26, true, 0.01",
            "3.00, true, 1.75"

    })
    void testMakePurchaseWithDifferentAmounts(double amount, boolean expectedPurchase, double expectedBalance) {
        // arrange
        VendingMachine vm = new VendingMachine();
        VendingMachineItem popcorn = new VendingMachineItem("popcorn", 1.25);
        vm.addItem(popcorn, "C");
        vm.insertMoney(amount);

        // act
        boolean purchased = vm.makePurchase("C");
        // assert
        assertEquals(expectedPurchase, purchased);
        assertEquals(expectedBalance, vm.getBalance(), 0.001);
        if (expectedPurchase) {
            assertNull(vm.getItem("C"));
        } else {
            assertSame(popcorn, vm.getItem("C"));
        }
    }
}
