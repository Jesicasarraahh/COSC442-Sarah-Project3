import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * JUnit 5 tests for VendingMachineItem.
 * Order for each behavior: valid, exception/invalid, boundary.
 */
public class VendingMachineItemTestAI {

	private static final double DELTA = 1e-9;

	// ---------------------------------------------------------------------
	// Constructor, getName(), getPrice()
	// ---------------------------------------------------------------------

	// ---- 1. Valid behavior ----

	@Test
	void testConstructor_validNameAndPrice_getNameReturnsName() {
		// Arrange / Act
		VendingMachineItem item = new VendingMachineItem("Chips", 1.25);
		// Assert
		assertEquals("Chips", item.getName());
	}

	@Test
	void testConstructor_validNameAndPrice_getPriceReturnsPrice() {
		// Arrange / Act
		VendingMachineItem item = new VendingMachineItem("Chips", 1.25);
		// Assert
		assertEquals(1.25, item.getPrice(), DELTA);
	}

	@Test
	void testGetters_calledRepeatedly_returnSameValues() {
		// Arrange
		VendingMachineItem item = new VendingMachineItem("Soda", 1.50);
		// Act
		item.getName();
		item.getPrice();
		// Assert
		assertEquals("Soda", item.getName());
		assertEquals(1.50, item.getPrice(), DELTA);
	}

	/** Equivalence classes of valid prices: zero, cents, dollars, large. */
	@ParameterizedTest
	@CsvSource({
			"Free Sample, 0.00",
			"Gum,         0.01",
			"Candy,       0.75",
			"Soda,        1.50",
			"Sandwich,    7.99",
			"Gift Box,    1000.00"
	})
	void testConstructor_validPrices_priceIsStored(String name, double price) {
		// Arrange / Act
		VendingMachineItem item = new VendingMachineItem(name, price);
		// Assert
		assertEquals(name, item.getName());
		assertEquals(price, item.getPrice(), DELTA);
	}

	// ---- 2. Exception / invalid behavior ----

	@Test
	void testConstructor_negativePrice_throwsException() {
		// Arrange / Act / Assert
		assertThrows(VendingMachineException.class, () -> new VendingMachineItem("Chips", -1.00));
	}

	@Test
	void testConstructor_negativePrice_messageDescribesProblem() {
		// Arrange / Act
		VendingMachineException ex = assertThrows(VendingMachineException.class,
				() -> new VendingMachineItem("Chips", -1.00));
		// Assert
		assertEquals("Price cannot be less than zero", ex.getMessage());
	}

	@ParameterizedTest
	@ValueSource(doubles = { -0.0001, -0.01, -0.99, -1.0, -50.0, -Double.MAX_VALUE })
	void testConstructor_negativePrices_throwException(double badPrice) {
		// Arrange / Act / Assert
		assertThrows(VendingMachineException.class, () -> new VendingMachineItem("Item", badPrice));
	}

	// ---- 3. Boundary behavior ----

	@Test
	void testConstructor_priceZero_isAccepted() {
		// Arrange / Act: 0 is the lowest valid price
		VendingMachineItem item = new VendingMachineItem("Free", 0.0);
		// Assert
		assertEquals(0.0, item.getPrice(), DELTA);
	}

	@Test
	void testConstructor_priceJustAboveZero_isAccepted() {
		// Arrange / Act
		VendingMachineItem item = new VendingMachineItem("Penny Candy", 0.01);
		// Assert
		assertEquals(0.01, item.getPrice(), DELTA);
	}

	@Test
	void testConstructor_priceJustBelowZero_throwsException() {
		// Arrange / Act / Assert
		assertThrows(VendingMachineException.class, () -> new VendingMachineItem("Bad", -0.01));
	}

	@Test
	void testConstructor_emptyName_isAccepted() {
		// Arrange / Act: spec places no restriction on the name
		VendingMachineItem item = new VendingMachineItem("", 1.00);
		// Assert
		assertEquals("", item.getName());
	}

	@Test
	void testConstructor_nullName_isStoredAsNull() {
		// Arrange / Act: current behavior; spec does not restrict name
		VendingMachineItem item = new VendingMachineItem(null, 1.00);
		// Assert
		assertNull(item.getName());
	}
}