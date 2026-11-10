import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * JUnit 5 tests for VendingMachine.
 *
 * Test design: for each public behavior the tests are ordered
 *   1. valid behavior, 2. exception/invalid behavior, 3. boundary behavior.
 *
 * Money is stored as a double, so every comparison of doubles uses DELTA.
 *
 * NOTE: this class lives in the default package (like the source files) so it
 * can call the protected getItem() method to inspect slot contents.
 */
public class VendingMachineTestAI {

	private static final double DELTA = 1e-9;

	// Price used by the purchase boundary tests
	private static final double SNACK_PRICE = 0.75;

	private VendingMachine machine;
	private VendingMachineItem chips; // $1.25
	private VendingMachineItem candy; // $0.75
	private VendingMachineItem gum; // $0.00 (free item, boundary price)

	@BeforeEach
	void setUp() {
		machine = new VendingMachine();
		chips = new VendingMachineItem("Chips", 1.25);
		candy = new VendingMachineItem("Candy", SNACK_PRICE);
		gum = new VendingMachineItem("Free Gum", 0.0);
	}

	// =====================================================================
	// Constructor / initial state
	// =====================================================================

	@Test
	void testConstants_slotCountAndCodes_matchSpecification() {
		// Arrange / Act / Assert
		assertEquals(4, VendingMachine.NUM_SLOTS);
		assertEquals("A", VendingMachine.A_CODE);
		assertEquals("B", VendingMachine.B_CODE);
		assertEquals("C", VendingMachine.C_CODE);
		assertEquals("D", VendingMachine.D_CODE);
	}

	@Test
	void testConstructor_newMachine_balanceIsZero() {
		// Arrange: machine built in setUp()
		// Act
		double balance = machine.getBalance();
		// Assert
		assertEquals(0.0, balance, DELTA);
	}

	@ParameterizedTest
	@ValueSource(strings = { "A", "B", "C", "D" })
	void testConstructor_newMachine_everySlotIsEmpty(String code) {
		// Arrange: machine built in setUp()
		// Act
		VendingMachineItem item = machine.getItem(code);
		// Assert
		assertNull(item);
	}

	// =====================================================================
	// addItem
	// =====================================================================

	// ---- 1. Valid behavior ----

	@ParameterizedTest
	@ValueSource(strings = { "A", "B", "C", "D" })
	void testAddItem_validCodeEmptySlot_itemStoredInThatSlot(String code) {
		// Arrange: empty machine
		// Act
		machine.addItem(chips, code);
		// Assert
		assertSame(chips, machine.getItem(code));
	}

	@Test
	void testAddItem_differentSlots_eachSlotHoldsItsOwnItem() {
		// Arrange / Act
		machine.addItem(chips, "A");
		machine.addItem(candy, "B");
		// Assert
		assertSame(chips, machine.getItem("A"));
		assertSame(candy, machine.getItem("B"));
	}

	@Test
	void testAddItem_toOneSlot_doesNotFillOtherSlots() {
		// Arrange / Act
		machine.addItem(chips, "B");
		// Assert
		assertNull(machine.getItem("A"));
		assertNull(machine.getItem("C"));
		assertNull(machine.getItem("D"));
	}

	@Test
	void testAddItem_toOneSlot_doesNotChangeBalance() {
		// Arrange
		machine.insertMoney(2.00);
		// Act
		machine.addItem(chips, "A");
		// Assert
		assertEquals(2.00, machine.getBalance(), DELTA);
	}

	@Test
	void testAddItem_allFourSlots_allAccepted() {
		// Arrange
		VendingMachineItem fourth = new VendingMachineItem("Soda", 1.50);
		// Act
		machine.addItem(chips, "A");
		machine.addItem(candy, "B");
		machine.addItem(gum, "C");
		machine.addItem(fourth, "D");
		// Assert
		assertSame(chips, machine.getItem("A"));
		assertSame(candy, machine.getItem("B"));
		assertSame(gum, machine.getItem("C"));
		assertSame(fourth, machine.getItem("D"));
	}

	// ---- 2. Exception / invalid behavior ----

	@Test
	void testAddItem_occupiedSlot_throwsException() {
		// Arrange
		machine.addItem(chips, "A");
		// Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.addItem(candy, "A"));
	}

	@Test
	void testAddItem_occupiedSlot_messageNamesTheSlot() {
		// Arrange
		machine.addItem(chips, "C");
		// Act
		VendingMachineException ex = assertThrows(VendingMachineException.class,
				() -> machine.addItem(candy, "C"));
		// Assert
		assertEquals("Slot C already occupied", ex.getMessage());
	}

	@Test
	void testAddItem_occupiedSlot_originalItemIsKept() {
		// Arrange
		machine.addItem(chips, "A");
		// Act
		assertThrows(VendingMachineException.class, () -> machine.addItem(candy, "A"));
		// Assert: the failed add must not overwrite the existing item
		assertSame(chips, machine.getItem("A"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "E", "a", "b", "d", "AA", "AB", "1", "Z", "", " ", "A " })
	void testAddItem_invalidCode_throwsException(String badCode) {
		// Arrange: empty machine
		// Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.addItem(chips, badCode));
	}

	@Test
	void testAddItem_invalidCode_messageDescribesInvalidCode() {
		// Arrange / Act
		VendingMachineException ex = assertThrows(VendingMachineException.class,
				() -> machine.addItem(chips, "E"));
		// Assert
		assertEquals("Invalid code for vending machine item", ex.getMessage());
	}

	@Test
	void testAddItem_invalidCode_noSlotIsModified() {
		// Arrange / Act
		assertThrows(VendingMachineException.class, () -> machine.addItem(chips, "E"));
		// Assert
		assertNull(machine.getItem("A"));
		assertNull(machine.getItem("B"));
		assertNull(machine.getItem("C"));
		assertNull(machine.getItem("D"));
	}

	@Test
	void testAddItem_nullCode_throwsRuntimeException() {
		// Arrange: spec does not define null codes; at minimum it must not succeed silently
		// Act / Assert
		assertThrows(RuntimeException.class, () -> machine.addItem(chips, null));
	}

	// ---- 3. Boundary behavior ----

	@Test
	void testAddItem_slotEmptiedByRemoveItem_canBeFilledAgain() {
		// Arrange: occupied -> empty boundary
		machine.addItem(chips, "A");
		machine.removeItem("A");
		// Act
		machine.addItem(candy, "A");
		// Assert
		assertSame(candy, machine.getItem("A"));
	}

	@Test
	void testAddItem_slotEmptiedByPurchase_canBeFilledAgain() {
		// Arrange
		machine.addItem(candy, "B");
		machine.insertMoney(SNACK_PRICE);
		assertTrue(machine.makePurchase("B"));
		// Act
		machine.addItem(chips, "B");
		// Assert
		assertSame(chips, machine.getItem("B"));
	}

	@Test
	void testAddItem_neighbourSlotOccupied_doesNotBlockAdjacentSlot() {
		// Arrange: boundary between adjacent slot indexes (A=0, B=1)
		machine.addItem(chips, "A");
		// Act
		machine.addItem(candy, "B");
		// Assert
		assertSame(candy, machine.getItem("B"));
	}

	// =====================================================================
	// getItem (protected, used to inspect state)
	// =====================================================================

	@Test
	void testGetItem_occupiedSlot_returnsThatItem() {
		// Arrange
		machine.addItem(candy, "D");
		// Act
		VendingMachineItem result = machine.getItem("D");
		// Assert
		assertSame(candy, result);
	}

	@Test
	void testGetItem_emptySlot_returnsNull() {
		// Arrange: empty machine
		// Act
		VendingMachineItem result = machine.getItem("A");
		// Assert
		assertNull(result);
	}

	@Test
	void testGetItem_doesNotRemoveItem() {
		// Arrange
		machine.addItem(chips, "A");
		// Act
		machine.getItem("A");
		// Assert
		assertNotNull(machine.getItem("A"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "E", "a", "", "AA", "0" })
	void testGetItem_invalidCode_throwsException(String badCode) {
		// Arrange / Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.getItem(badCode));
	}

	// =====================================================================
	// removeItem
	// =====================================================================

	// ---- 1. Valid behavior ----

	@ParameterizedTest
	@ValueSource(strings = { "A", "B", "C", "D" })
	void testRemoveItem_occupiedSlot_returnsTheItem(String code) {
		// Arrange
		machine.addItem(chips, code);
		// Act
		VendingMachineItem removed = machine.removeItem(code);
		// Assert
		assertSame(chips, removed);
	}

	@Test
	void testRemoveItem_occupiedSlot_slotBecomesEmpty() {
		// Arrange
		machine.addItem(chips, "A");
		// Act
		machine.removeItem("A");
		// Assert
		assertNull(machine.getItem("A"));
	}

	@Test
	void testRemoveItem_oneSlot_leavesOtherItemsUntouched() {
		// Arrange
		machine.addItem(chips, "A");
		machine.addItem(candy, "B");
		// Act
		machine.removeItem("A");
		// Assert
		assertSame(candy, machine.getItem("B"));
	}

	@Test
	void testRemoveItem_occupiedSlot_doesNotChangeBalance() {
		// Arrange
		machine.addItem(chips, "A");
		machine.insertMoney(3.00);
		// Act
		machine.removeItem("A");
		// Assert
		assertEquals(3.00, machine.getBalance(), DELTA);
	}

	// ---- 2. Exception / invalid behavior ----

	@Test
	void testRemoveItem_emptySlot_throwsException() {
		// Arrange: empty machine
		// Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.removeItem("A"));
	}

	@Test
	void testRemoveItem_emptySlot_messageNamesTheSlot() {
		// Arrange / Act
		VendingMachineException ex = assertThrows(VendingMachineException.class,
				() -> machine.removeItem("B"));
		// Assert
		assertEquals("Slot B is empty -- cannot remove item", ex.getMessage());
	}

	@Test
	void testRemoveItem_sameSlotTwice_secondRemoveThrowsException() {
		// Arrange
		machine.addItem(chips, "C");
		machine.removeItem("C");
		// Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.removeItem("C"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "E", "a", "d", "AA", "", " ", "9" })
	void testRemoveItem_invalidCode_throwsException(String badCode) {
		// Arrange: all valid slots filled so only the code can be at fault
		machine.addItem(chips, "A");
		machine.addItem(chips, "B");
		machine.addItem(chips, "C");
		machine.addItem(chips, "D");
		// Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.removeItem(badCode));
	}

	@Test
	void testRemoveItem_invalidCode_messageDescribesInvalidCode() {
		// Arrange / Act
		VendingMachineException ex = assertThrows(VendingMachineException.class,
				() -> machine.removeItem("X"));
		// Assert
		assertEquals("Invalid code for vending machine item", ex.getMessage());
	}

	// ---- 3. Boundary behavior ----

	@Test
	void testRemoveItem_lastItemInMachine_machineBecomesEmpty() {
		// Arrange: full -> empty machine boundary
		machine.addItem(chips, "A");
		machine.addItem(candy, "B");
		machine.addItem(gum, "C");
		machine.addItem(chips, "D");
		// Act
		machine.removeItem("A");
		machine.removeItem("B");
		machine.removeItem("C");
		machine.removeItem("D");
		// Assert
		assertNull(machine.getItem("A"));
		assertNull(machine.getItem("B"));
		assertNull(machine.getItem("C"));
		assertNull(machine.getItem("D"));
	}

	@Test
	void testRemoveItem_emptySlotNextToOccupiedSlot_throwsAndNeighbourKept() {
		// Arrange: A occupied, B empty (adjacent boundary)
		machine.addItem(chips, "A");
		// Act
		assertThrows(VendingMachineException.class, () -> machine.removeItem("B"));
		// Assert
		assertSame(chips, machine.getItem("A"));
	}

	// =====================================================================
	// insertMoney
	// =====================================================================

	// ---- 1. Valid behavior ----

	@Test
	void testInsertMoney_validAmount_increasesBalance() {
		// Arrange: balance is 0
		// Act
		machine.insertMoney(1.50);
		// Assert
		assertEquals(1.50, machine.getBalance(), DELTA);
	}

	@Test
	void testInsertMoney_multipleInsertions_balanceAccumulates() {
		// Arrange
		machine.insertMoney(1.00);
		// Act
		machine.insertMoney(0.25);
		machine.insertMoney(0.10);
		// Assert
		assertEquals(1.35, machine.getBalance(), DELTA);
	}

	/**
	 * Equivalence classes for valid amounts: zero, sub-dollar, whole dollars,
	 * fractional dollars, and a large amount.
	 */
	@ParameterizedTest
	@CsvSource({
			"0.00,    0.00",
			"0.01,    0.01",
			"0.25,    0.25",
			"1.00,    1.00",
			"5.50,    5.50",
			"20.00,   20.00",
			"1000.75, 1000.75"
	})
	void testInsertMoney_validAmountsFromEmptyBalance_balanceEqualsAmount(double amount, double expectedBalance) {
		// Arrange: fresh machine, balance 0
		// Act
		machine.insertMoney(amount);
		// Assert
		assertEquals(expectedBalance, machine.getBalance(), DELTA);
	}

	// ---- 2. Exception / invalid behavior ----

	/**
	 * Equivalence class for invalid amounts: everything below 0, from the value
	 * just below the boundary to very large negatives.
	 */
	@ParameterizedTest
	@ValueSource(doubles = { -0.0001, -0.01, -0.50, -1.0, -20.0, -1000000.0, -Double.MAX_VALUE })
	void testInsertMoney_negativeAmount_throwsException(double amount) {
		// Arrange: fresh machine
		// Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.insertMoney(amount));
	}

	@Test
	void testInsertMoney_negativeAmount_messageDescribesInvalidAmount() {
		// Arrange / Act
		VendingMachineException ex = assertThrows(VendingMachineException.class,
				() -> machine.insertMoney(-1.00));
		// Assert
		assertEquals("Invalid amount.  Amount must be >= 0", ex.getMessage());
	}

	@Test
	void testInsertMoney_negativeAmount_balanceUnchanged() {
		// Arrange
		machine.insertMoney(2.00);
		// Act
		assertThrows(VendingMachineException.class, () -> machine.insertMoney(-0.50));
		// Assert
		assertEquals(2.00, machine.getBalance(), DELTA);
	}

	/**
	 * The specification says amount must be >= 0. NaN is not >= 0, so it should be
	 * rejected. The current implementation only checks "amount < 0", which is false
	 * for NaN, so NaN is accepted and corrupts the balance. This test is expected to
	 * FAIL against the current code and documents a genuine defect.
	 */
	@Test
	void testInsertMoney_notANumber_throwsException() {
		// Arrange / Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.insertMoney(Double.NaN));
	}

	// ---- 3. Boundary behavior ----

	@Test
	void testInsertMoney_zeroAmount_isAcceptedAndBalanceUnchanged() {
		// Arrange
		machine.insertMoney(1.00);
		// Act
		machine.insertMoney(0.0);
		// Assert: 0 is the lowest valid value
		assertEquals(1.00, machine.getBalance(), DELTA);
	}

	@Test
	void testInsertMoney_justAboveZero_isAccepted() {
		// Arrange / Act
		machine.insertMoney(0.01);
		// Assert
		assertEquals(0.01, machine.getBalance(), DELTA);
	}

	@Test
	void testInsertMoney_justBelowZero_isRejected() {
		// Arrange / Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.insertMoney(-0.01));
	}

	@Test
	void testInsertMoney_afterRejectedAmount_machineStillAcceptsValidMoney() {
		// Arrange
		assertThrows(VendingMachineException.class, () -> machine.insertMoney(-5.00));
		// Act
		machine.insertMoney(1.00);
		// Assert
		assertEquals(1.00, machine.getBalance(), DELTA);
	}

	// =====================================================================
	// getBalance
	// =====================================================================

	@Test
	void testGetBalance_afterInsertingMoney_returnsInsertedTotal() {
		// Arrange
		machine.insertMoney(2.00);
		machine.insertMoney(0.75);
		// Act
		double balance = machine.getBalance();
		// Assert
		assertEquals(2.75, balance, DELTA);
	}

	@Test
	void testGetBalance_calledRepeatedly_doesNotChangeBalance() {
		// Arrange
		machine.insertMoney(1.25);
		// Act
		machine.getBalance();
		machine.getBalance();
		double balance = machine.getBalance();
		// Assert (postcondition: balance remains the same)
		assertEquals(1.25, balance, DELTA);
	}

	@Test
	void testGetBalance_afterFailedInsert_returnsPreviousBalance() {
		// Arrange
		machine.insertMoney(1.00);
		assertThrows(VendingMachineException.class, () -> machine.insertMoney(-1.00));
		// Act
		double balance = machine.getBalance();
		// Assert
		assertEquals(1.00, balance, DELTA);
	}

	@Test
	void testGetBalance_afterPurchase_reflectsReducedBalance() {
		// Arrange
		machine.addItem(candy, "A");
		machine.insertMoney(2.00);
		machine.makePurchase("A");
		// Act
		double balance = machine.getBalance();
		// Assert
		assertEquals(1.25, balance, DELTA);
	}

	@Test
	void testGetBalance_afterReturnChange_isZero() {
		// Arrange
		machine.insertMoney(4.00);
		machine.returnChange();
		// Act
		double balance = machine.getBalance();
		// Assert
		assertEquals(0.0, balance, DELTA);
	}

	// =====================================================================
	// makePurchase
	// =====================================================================

	// ---- 1. Valid behavior ----

	@Test
	void testMakePurchase_sufficientBalance_returnsTrue() {
		// Arrange
		machine.addItem(chips, "A"); // $1.25
		machine.insertMoney(2.00);
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertTrue(result);
	}

	@Test
	void testMakePurchase_sufficientBalance_subtractsPriceFromBalance() {
		// Arrange
		machine.addItem(chips, "A"); // $1.25
		machine.insertMoney(2.00);
		// Act
		machine.makePurchase("A");
		// Assert
		assertEquals(0.75, machine.getBalance(), DELTA);
	}

	@Test
	void testMakePurchase_sufficientBalance_emptiesTheSlot() {
		// Arrange
		machine.addItem(chips, "A");
		machine.insertMoney(2.00);
		// Act
		machine.makePurchase("A");
		// Assert
		assertNull(machine.getItem("A"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "A", "B", "C", "D" })
	void testMakePurchase_eachValidSlot_completesPurchase(String code) {
		// Arrange
		machine.addItem(candy, code);
		machine.insertMoney(1.00);
		// Act
		boolean result = machine.makePurchase(code);
		// Assert
		assertTrue(result);
		assertNull(machine.getItem(code));
		assertEquals(0.25, machine.getBalance(), DELTA);
	}

	@Test
	void testMakePurchase_oneSlot_doesNotAffectOtherSlots() {
		// Arrange
		machine.addItem(chips, "A");
		machine.addItem(candy, "B");
		machine.insertMoney(5.00);
		// Act
		machine.makePurchase("A");
		// Assert
		assertSame(candy, machine.getItem("B"));
	}

	@Test
	void testMakePurchase_twoPurchases_balanceReducedByBothPrices() {
		// Arrange
		machine.addItem(chips, "A"); // 1.25
		machine.addItem(candy, "B"); // 0.75
		machine.insertMoney(3.00);
		// Act
		boolean first = machine.makePurchase("A");
		boolean second = machine.makePurchase("B");
		// Assert
		assertTrue(first);
		assertTrue(second);
		assertEquals(1.00, machine.getBalance(), DELTA);
	}

	// ---- 2. Exception / invalid behavior ----

	@Test
	void testMakePurchase_insufficientBalance_returnsFalse() {
		// Arrange
		machine.addItem(chips, "A"); // 1.25
		machine.insertMoney(1.00);
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertFalse(result);
	}

	@Test
	void testMakePurchase_insufficientBalance_balanceUnchanged() {
		// Arrange
		machine.addItem(chips, "A");
		machine.insertMoney(1.00);
		// Act
		machine.makePurchase("A");
		// Assert
		assertEquals(1.00, machine.getBalance(), DELTA);
	}

	@Test
	void testMakePurchase_insufficientBalance_itemStaysInSlot() {
		// Arrange
		machine.addItem(chips, "A");
		machine.insertMoney(1.00);
		// Act
		machine.makePurchase("A");
		// Assert
		assertSame(chips, machine.getItem("A"));
	}

	@Test
	void testMakePurchase_noMoneyInserted_returnsFalse() {
		// Arrange
		machine.addItem(chips, "A");
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertFalse(result);
	}

	@Test
	void testMakePurchase_emptySlot_returnsFalse() {
		// Arrange: plenty of money, but nothing in slot A
		machine.insertMoney(10.00);
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertFalse(result);
	}

	@Test
	void testMakePurchase_emptySlot_balanceUnchanged() {
		// Arrange
		machine.insertMoney(10.00);
		// Act
		machine.makePurchase("A");
		// Assert
		assertEquals(10.00, machine.getBalance(), DELTA);
	}

	@Test
	void testMakePurchase_sameSlotTwice_secondAttemptReturnsFalse() {
		// Arrange
		machine.addItem(candy, "A");
		machine.insertMoney(5.00);
		machine.makePurchase("A");
		// Act
		boolean second = machine.makePurchase("A");
		// Assert
		assertFalse(second);
	}

	@Test
	void testMakePurchase_sameSlotTwice_secondAttemptDoesNotChargeAgain() {
		// Arrange
		machine.addItem(candy, "A");
		machine.insertMoney(5.00);
		machine.makePurchase("A");
		// Act
		machine.makePurchase("A");
		// Assert: charged only once
		assertEquals(4.25, machine.getBalance(), DELTA);
	}

	@ParameterizedTest
	@ValueSource(strings = { "E", "a", "AA", "", " ", "5" })
	void testMakePurchase_invalidCode_throwsException(String badCode) {
		// Arrange: spec does not mention invalid codes for makePurchase, but it
		// delegates to getItem() which throws VendingMachineException.
		machine.addItem(candy, "A");
		machine.insertMoney(5.00);
		// Act / Assert
		assertThrows(VendingMachineException.class, () -> machine.makePurchase(badCode));
	}

	@Test
	void testMakePurchase_invalidCode_balanceUnchanged() {
		// Arrange
		machine.insertMoney(5.00);
		// Act
		assertThrows(VendingMachineException.class, () -> machine.makePurchase("Q"));
		// Assert
		assertEquals(5.00, machine.getBalance(), DELTA);
	}

	// ---- 3. Boundary behavior ----

	@Test
	void testMakePurchase_exactBalance_completesPurchase() {
		// Arrange: balance == price
		machine.addItem(candy, "A"); // 0.75
		machine.insertMoney(0.75);
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertTrue(result);
	}

	@Test
	void testMakePurchase_exactBalance_leavesZeroBalance() {
		// Arrange
		machine.addItem(candy, "A");
		machine.insertMoney(0.75);
		// Act
		machine.makePurchase("A");
		// Assert
		assertEquals(0.0, machine.getBalance(), DELTA);
	}

	@Test
	void testMakePurchase_oneCentBelowPrice_isRejected() {
		// Arrange
		machine.addItem(candy, "A");
		machine.insertMoney(0.74);
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertFalse(result);
	}

	@Test
	void testMakePurchase_oneCentAbovePrice_completesAndLeavesOneCent() {
		// Arrange
		machine.addItem(candy, "A");
		machine.insertMoney(0.76);
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertTrue(result);
		assertEquals(0.01, machine.getBalance(), 1e-6);
	}

	@Test
	void testMakePurchase_freeItemWithZeroBalance_completesPurchase() {
		// Arrange: price 0 and balance 0 (balance >= price at its lowest point)
		machine.addItem(gum, "C");
		// Act
		boolean result = machine.makePurchase("C");
		// Assert
		assertTrue(result);
		assertNull(machine.getItem("C"));
		assertEquals(0.0, machine.getBalance(), DELTA);
	}

	@Test
	void testMakePurchase_balanceBuiltFromSeveralInsertions_exactTotalCompletesPurchase() {
		// Arrange: 0.25 + 0.25 + 0.25 == 0.75 exactly representable in binary
		machine.addItem(candy, "A");
		machine.insertMoney(0.25);
		machine.insertMoney(0.25);
		machine.insertMoney(0.25);
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertTrue(result);
	}

	@Test
	void testMakePurchase_slotRefilledAfterPurchase_canBePurchasedAgain() {
		// Arrange
		machine.addItem(candy, "A");
		machine.insertMoney(2.00);
		machine.makePurchase("A");
		machine.addItem(chips, "A");
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertTrue(result);
		assertEquals(0.00, machine.getBalance(), DELTA);
	}

	/**
	 * Boundary / equivalence-class table for the purchase decision.
	 * The item always costs 0.75. Rows cover: nothing inserted, well below,
	 * immediately below, exactly at, immediately above, and well above the price.
	 */
	@ParameterizedTest(name = "balance {0} vs price 0.75 -> purchase {1}, remaining {2}")
	@CsvSource({
			"0.00, false, 0.00",
			"0.50, false, 0.50",
			"0.74, false, 0.74",
			"0.75, true,  0.00",
			"0.76, true,  0.01",
			"1.00, true,  0.25",
			"5.00, true,  4.25"
	})
	void testMakePurchase_balanceVersusPrice_boundaryTable(double balance, boolean expectedResult,
			double expectedRemaining) {
		// Arrange
		machine.addItem(candy, "A");
		machine.insertMoney(balance);
		// Act
		boolean result = machine.makePurchase("A");
		// Assert
		assertEquals(expectedResult, result);
		assertEquals(expectedRemaining, machine.getBalance(), 1e-6);
		if (expectedResult) {
			assertNull(machine.getItem("A"));
		} else {
			assertSame(candy, machine.getItem("A"));
		}
	}

	// =====================================================================
	// returnChange
	// =====================================================================

	// ---- 1. Valid behavior ----

	@Test
	void testReturnChange_withBalance_returnsFullBalance() {
		// Arrange
		machine.insertMoney(3.50);
		// Act
		double change = machine.returnChange();
		// Assert
		assertEquals(3.50, change, DELTA);
	}

	@Test
	void testReturnChange_withBalance_resetsBalanceToZero() {
		// Arrange
		machine.insertMoney(3.50);
		// Act
		machine.returnChange();
		// Assert
		assertEquals(0.0, machine.getBalance(), DELTA);
	}

	@Test
	void testReturnChange_afterPurchase_returnsRemainingMoney() {
		// Arrange
		machine.addItem(chips, "A"); // 1.25
		machine.insertMoney(2.00);
		machine.makePurchase("A");
		// Act
		double change = machine.returnChange();
		// Assert
		assertEquals(0.75, change, DELTA);
	}

	@Test
	void testReturnChange_doesNotRemoveItems() {
		// Arrange
		machine.addItem(chips, "A");
		machine.insertMoney(1.00);
		// Act
		machine.returnChange();
		// Assert
		assertSame(chips, machine.getItem("A"));
	}

	// ---- 2. Exception / invalid behavior ----
	// returnChange() is not documented to throw for any input or state, so the
	// "invalid" scenario is calling it when there is nothing to return.

	@Test
	void testReturnChange_newMachineNoMoney_returnsZeroWithoutException() {
		// Arrange: fresh machine
		// Act
		double change = machine.returnChange();
		// Assert
		assertEquals(0.0, change, DELTA);
	}

	@Test
	void testReturnChange_calledTwice_secondCallReturnsZero() {
		// Arrange
		machine.insertMoney(2.00);
		machine.returnChange();
		// Act
		double secondChange = machine.returnChange();
		// Assert: money cannot be refunded twice
		assertEquals(0.0, secondChange, DELTA);
	}

	// ---- 3. Boundary behavior ----

	@Test
	void testReturnChange_balanceExactlyZeroAfterExactPurchase_returnsZero() {
		// Arrange
		machine.addItem(candy, "A");
		machine.insertMoney(0.75);
		machine.makePurchase("A");
		// Act
		double change = machine.returnChange();
		// Assert
		assertEquals(0.0, change, DELTA);
	}

	@Test
	void testReturnChange_smallestUnitOfMoney_returnsOneCent() {
		// Arrange
		machine.insertMoney(0.01);
		// Act
		double change = machine.returnChange();
		// Assert
		assertEquals(0.01, change, DELTA);
	}

	@Test
	void testReturnChange_thenInsertMoney_balanceStartsFromZeroAgain() {
		// Arrange
		machine.insertMoney(5.00);
		machine.returnChange();
		// Act
		machine.insertMoney(1.00);
		// Assert
		assertEquals(1.00, machine.getBalance(), DELTA);
	}

	@Test
	void testReturnChange_insertZeroThenReturn_returnsZero() {
		// Arrange: zero is a valid insertion but adds nothing
		machine.insertMoney(0.0);
		// Act
		double change = machine.returnChange();
		// Assert
		assertEquals(0.0, change, DELTA);
	}
}