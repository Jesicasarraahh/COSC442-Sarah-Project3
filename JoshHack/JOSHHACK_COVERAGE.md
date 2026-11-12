# JoshHack Inventory Coverage Report

## Baseline

The downloaded project contained no existing test files. Therefore,
there was no existing test suite available for a baseline coverage run.
Baseline coverage was not measured.

## Final Coverage

| Source File | Statement Coverage | Branch Coverage |
|---|---|---|
| Inventory.java | 24/24 (100%) | 18/18 (100%) |

All 15 tests in InventoryTest passed.

## Tests Added

The tests check inventory initialization, adding one or multiple items,
adding null, and attempting to add an item when all five slots are full.
The full-inventory test verifies that the additional item is not stored
and that the original contents remain unchanged.

Other tests check searching for present and missing items, searching
for null, retrieving individual items, retrieving the contents of a
populated or empty inventory, and accessing an invalid index. The suite
also checks whether the inventory is full, removing an existing item,
and attempting to remove an item that is not present.

## Observations

The contains method compares object references using ==. Consequently,
contains(null) returns true when an empty slot exists. The null-search
test records this implementation behavior; no documentation specifies
a different requirement.

Adding null leaves an empty slot empty. The test verifies that an
existing item remains unchanged.

## Remaining Uncovered Code

Nothing remained uncovered, 100% coverage was done for Inventory.java.




