import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Additional tests that target gaps a 100% statement/branch report cannot show.
 * These methods can be pasted into VendingMachineTest as-is (same setUp fields)
 * or kept as this separate class.
 */
public class VendingMachineAdditionalTestAI {

	private static final double DELTA = 1e-9;

	private VendingMachine machine;
	private VendingMachineItem chips; // $1.25

	@BeforeEach
	void setUp() {
		machine = new VendingMachine();
		chips = new VendingMachineItem("Chips", 1.25);
	}

	// ---------------------------------------------------------------------
	// 1. Price of the REQUESTED slot is the one that is checked and charged
	// ---------------------------------------------------------------------

	/**
	 * Full machine with four different prices: A=1.25, B=0.75, C=0.00, D=2.50.
	 * For every slot: exact balance, one cent short. The A/0.75 row is
	 * affordable for slot B but not for slot A, so it fails if the code ever
	 * compares against or charges the wrong slot's price.
	 */
	@ParameterizedTest(name = "slot {0}, balance {1} -> purchase {2}, remaining {3}")
	@CsvSource({
			"A, 1.25, true,  0.00",
			"A, 1.24, false, 1.24",
			"A, 0.75, false, 0.75",
			"B, 0.75, true,  0.00",
			"B, 0.74, false, 0.74",
			"C, 0.00, true,  0.00",
			"D, 2.50, true,  0.00",
			"D, 2.49, false, 2.49"
	})
	void testMakePurchase_distinctPricesInEverySlot_usesOnlyRequestedSlotPrice(String code, double balance,
			boolean expectedResult, double expectedRemaining) {
		// Arrange
		machine.addItem(new VendingMachineItem("A-item", 1.25), "A");
		machine.addItem(new VendingMachineItem("B-item", 0.75), "B");
		machine.addItem(new VendingMachineItem("C-item", 0.00), "C");
		machine.addItem(new VendingMachineItem("D-item", 2.50), "D");
		machine.insertMoney(balance);
		// Act
		boolean result = machine.makePurchase(code);
		// Assert
		assertEquals(expectedResult, result);
		assertEquals(expectedRemaining, machine.getBalance(), 1e-6);
		assertEquals(expectedResult, machine.getItem(code) == null);
	}

	// ---------------------------------------------------------------------
	// 2. State survives a failed purchase (retry after topping up)
	// ---------------------------------------------------------------------

	@Test
	void testMakePurchase_failedThenTopUp_secondAttemptSucceeds() {
		// Arrange: first attempt is 25 cents short
		machine.addItem(chips, "A");
		machine.insertMoney(1.00);
		assertFalse(machine.makePurchase("A"));
		// Act
		machine.insertMoney(0.25);
		boolean result = machine.makePurchase("A");
		// Assert
		assertTrue(result);
		assertEquals(0.0, machine.getBalance(), DELTA);
		assertNull(machine.getItem("A"));
	}

	// ---------------------------------------------------------------------
	// 3. Same item object stocked in two slots (slots are independent)
	// ---------------------------------------------------------------------

	@Test
	void testRemoveItem_sameObjectInTwoSlots_removesOnlyRequestedSlot() {
		// Arrange
		machine.addItem(chips, "A");
		machine.addItem(chips, "B");
		// Act
		machine.removeItem("A");
		// Assert
		assertNull(machine.getItem("A"));
		assertSame(chips, machine.getItem("B"));
	}

	@Test
	void testMakePurchase_sameObjectInTwoSlots_otherSlotStillPurchasable() {
		// Arrange
		machine.addItem(chips, "A");
		machine.addItem(chips, "B");
		machine.insertMoney(2.50);
		// Act
		boolean first = machine.makePurchase("A");
		boolean second = machine.makePurchase("B");
		// Assert
		assertTrue(first);
		assertTrue(second);
		assertEquals(0.0, machine.getBalance(), DELTA);
	}

	// ---------------------------------------------------------------------
	// 4. Negative infinity is a "negative amount" (spec: amount < 0 -> throws)
	// ---------------------------------------------------------------------

	@Test
	void testInsertMoney_negativeInfinity_throwsException() {
		// Arrange / Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.insertMoney(Double.NEGATIVE_INFINITY));
	}

	// ---------------------------------------------------------------------
	// 5. Floating-point accumulation vs. exact >= comparison
	// ---------------------------------------------------------------------

	/**
	 * Ten dimes are exactly one dollar, and makePurchase is documented to return
	 * true "if there is enough money". In IEEE-754 doubles, ten additions of 0.10
	 * give 0.9999999999999999, which is < 1.00, so the current code refuses the
	 * purchase. This test is expected to FAIL against the current code.
	 */
	@Test
	void testMakePurchase_tenDimesForOneDollarItem_completesPurchase() {
		// Arrange
		machine.addItem(new VendingMachineItem("Dollar Item", 1.00), "A");
		for (int i = 0; i < 10; i++) {
			machine.insertMoney(0.10);
		}
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertTrue(result);
	}
}