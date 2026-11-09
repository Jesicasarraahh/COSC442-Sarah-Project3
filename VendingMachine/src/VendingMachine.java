/**
 * 
 * This class represents the vending machine module. You can put items in the
 * vending machine,
 * remove items from the vending machine, purchase items from the vending
 * machine, and even get your
 * change back. The vending machine has 4 slots and each slot holds 1 item. Once
 * that item is bought,
 * that slot is empty. The slots are referred to by their "code", which is a
 * letter. These codes are
 * "A" for the slot at index 0, "B" for the slot at index 1, "C" for the slot at
 * index 2, and "D"
 * for the slot at index 3.
 */
public class VendingMachine {

	// The number of slots in the vending machine
	public static final int NUM_SLOTS = 4;

	// The code for the "A" slot
	public static final String A_CODE = "A";

	// The code for the "B" slot
	public static final String B_CODE = "B";

	// The code for the "C" slot
	public static final String C_CODE = "C";

	// The code for the "D" slot
	public static final String D_CODE = "D";

	// The initial balance of the vending machine
	private static final double INITIAL_BALANCE = 0;

	// Error message for invalid code
	private static final String INVALID_CODE_MESSAGE = "Invalid code for vending machine item";

	// Error message for amounts < 0
	private static final String INVALID_AMOUNT_MESSAGE = "Invalid amount.  Amount must be >= 0";

	// Slot part of error message
	private static final String SLOT_MESSAGE = "Slot ";

	// Already occupied part of error message
	private static final String ALREADY_OCCUPIED_MESSAGE = " already occupied";

	// "is empty" part of error message
	private static final String IS_EMPTY_MESSAGE = " is empty -- cannot remove item";

	// The amount of money in the vending machine currently
	protected double balance;

	// Array items in the vending machine
	private VendingMachineItem[] itemArray;

	/**
	 * Default constructor for the vending machine. It sets all the entries in
	 * the itemArray to be null, corresponding to an empty vending machine.
	 * Postcondition: all entries in itemArray are null, balance set to be 0
	 */
	public VendingMachine() {
		itemArray = new VendingMachineItem[NUM_SLOTS];
		for (int i = 0; i < NUM_SLOTS; i++) {
			itemArray[i] = null;
		}
		this.balance = INITIAL_BALANCE;
	}

	/**
	 * Gets the slot index given the code for that slot. Specifically, the codes are
	 * "A" for
	 * slot index 0, "B" for slot index 1, "C" for slot index 2 and "D" for slot
	 * index 3.
	 * 
	 * @param code The code for the slot.
	 * @return The slot index
	 * @throws VendingMachineException
	 */
	private int getSlotIndex(String code) throws VendingMachineException {
		if (code.equals(A_CODE)) {
			return 0;
		} else if (code.equals(B_CODE)) {
			return 1;
		} else if (code.equals(C_CODE)) {
			return 2;
		} else if (code.equals(D_CODE)) {
			return 3;
		} else {
			throw new VendingMachineException(VendingMachine.INVALID_CODE_MESSAGE);
		}
	}

	/**
	 * Adds an item to the vending machine at the slot specified by the code.
	 * 
	 * Precondition: The slot specified by the code must be empty
	 * Postcondition: The item is now at the slot specified by the code
	 * 
	 * @param item
	 *             The vending machine item object to add to the vending machine
	 * @param code
	 *             The code of where this item should be in the vending machine
	 * @throws VendingMachineException under the following conditions:
	 *                                 1. If you add an item to a slot that is
	 *                                 already occupied.
	 *                                 2. If you add an item with an invalid code
	 */
	public void addItem(VendingMachineItem item, String code)
			throws VendingMachineException {
		int slotIndex = getSlotIndex(code);
		if (itemArray[slotIndex] != null) {
			throw new VendingMachineException(SLOT_MESSAGE + code
					+ ALREADY_OCCUPIED_MESSAGE);
		} else {
			itemArray[slotIndex] = item;
		}
	}

	/**
	 * Gets the item occupying the slot with the given code.
	 * 
	 * @param code The code for the the slot in the vending machine eg. "A"
	 * @return The item occupying the slot with the given code
	 * @throws VendingMachineException if the code is invalid
	 */
	protected VendingMachineItem getItem(String code) throws VendingMachineException {
		int slotIndex = getSlotIndex(code);
		return itemArray[slotIndex];
	}

	/**
	 * Removes an item from the vending machine given its code.
	 * Postcondition: If the code slot is not empty, the item in that slot is
	 * removed.
	 * 
	 * @param code The code eg. "A" of the item in the vending machine
	 * @return The item occupying the slot with the given code.
	 * @throws VendingMachineException If the slot at the specified code is empty
	 *                                 and if the code is invalid
	 */
	public VendingMachineItem removeItem(String code) throws VendingMachineException {
		int slotIndex = getSlotIndex(code);
		VendingMachineItem item = itemArray[slotIndex];
		itemArray[slotIndex] = null;
		if (item == null) {
			throw new VendingMachineException(SLOT_MESSAGE + code + IS_EMPTY_MESSAGE);
		}
		return item;
	}

	/**
	 * Function to put money into the vending machine.
	 * Precondition: amount >= 0
	 * Postcondition: balance is now the previous balance + amount.
	 * 
	 * @param amount The amount of money to put in the vending machine
	 * @throws VendingMachineException Throws a VendingMachineException if the
	 *                                 amount is < 0
	 */
	public void insertMoney(double amount) throws VendingMachineException {
		//rejecting negative amounts of money
		//allowing 0 amount of money to be inserted
		//allowing positive amounts of money to be inserted
		if (amount < 0)
			throw new VendingMachineException(VendingMachine.INVALID_AMOUNT_MESSAGE);
		this.balance += amount;
	}

	/**
	 * Returns the amount of change the user has in the vending machine. Note that
	 * this simply
	 * returns the amount and does not actually give back the change to the user.
	 * Precondition: balance >= 0; Note that the vending machine should start with a
	 * 0 balance.
	 * Postcondition: the balance is >= 0 and remains the same as it was before the
	 * function was called.
	 * 
	 * @return The balance in the vending machine
	 */
	public double getBalance() {
		return this.balance;
	}

	/**
	 * This function attempts to purchase the item with the given code from the
	 * vending machine.
	 * Precondition: balance >= 0
	 * Postcondition: The amount of the item is subtracted from the balance
	 * 
	 * @param code The code for the item from the vending machine
	 * @return Returns true if there is enough money to make the purchase. Returns
	 *         false if not enough money is put
	 *         into the vending machine to make the purchase. Also returns false if
	 *         the code is for an empty slot.
	 */
	public boolean makePurchase(String code) {
		boolean returnCode = false;
		VendingMachineItem item = getItem(code);
		if ((item != null) && (this.balance >= item.getPrice())) {
			removeItem(code);
			this.balance -= item.getPrice();
			returnCode = true;
		}
		return returnCode;
	}

	/**
	 * Returns the amount of change in the machine and sets the balance to 0.
	 * Precondition: balance >= 0
	 * Postcondition: balance = 0
	 * 
	 * @return The amount of change in the machine
	 */
	public double returnChange() {
		double change = this.balance;
		this.balance = 0;
		return change;
	}
}
