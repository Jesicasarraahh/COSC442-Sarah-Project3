package game;

import org.junit.jupiter.api.Test;
import java.awt.Color;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


public class InventoryTest {
    private Inventory inventory;
    private Item apple;
    private Item knife;
    
    @BeforeEach
    void setUp() {
        inventory = new Inventory(5);
        apple = new Item('a', Color.RED, "Apple", "Apple");
        knife = new Item('k', Color.GRAY, "Knife", "Knife");
    }

    @Test 
    void testInventoryInitialization() {
        //act
        Item[] items = inventory.getItems();
        //assert
        assertEquals(5, items.length);
        assertNull(inventory.get(0));
        assertNull(inventory.get(1));
        assertFalse(inventory.isFull());

    }


    @Test
    void testAddItem() {
        //act
        inventory.add(apple);
        //assert
        assertSame(apple, inventory.get(0));
        assertNull(inventory.get(1));
        assertFalse(inventory.isFull());
       

    }

    @Test 
    void testAddNullItem() {
        //act
        inventory.add(apple);
        //assert
        inventory.add(null);
        //assert
        assertSame(apple, inventory.get(0));
        assertNull(inventory.get(1));
        assertFalse(inventory.isFull());
    }

    @Test 
    void testAddTwoItems() {
        //act
        inventory.add(apple);
        inventory.add(knife);
        //assert
        assertSame(apple, inventory.get(0));
        assertSame(knife, inventory.get(1));
        assertFalse(inventory.isFull());

    }

    @Test 
    void testAddItemToFullInventory() {
        //arrange filling up the inventory with 5 items
        inventory.add(apple);
        inventory.add(knife);
        inventory.add(new Item('b', Color.WHITE, "Bandage", "Bandage"));
        inventory.add(new Item('p', Color.BLUE, "Pistol", "Pistol"));
        inventory.add(new Item('c', Color.BLACK, "Car", "Car"));
        
        assertTrue(inventory.isFull());
        Item[] itemsBefore = inventory.getItems().clone();
        Item watermelon = new Item('w', Color.GREEN, "Watermelon", "Watermelon");
        //act
        inventory.add(watermelon);
        //assert
        assertFalse(inventory.contains(watermelon));
        assertArrayEquals(itemsBefore, inventory.getItems());
        assertTrue(inventory.isFull());
    }

    @Test
    void testContains() {
        //arrange
        inventory.add(apple);
        inventory.add(knife);
        //act
        boolean result = inventory.contains(knife);
        //assert
        assertTrue(result);

    }

    @Test 
    void testContainsNull() {
        //arrange
        inventory.add(apple);
        inventory.add(knife);
        //act
        boolean result = inventory.contains(null);
        //assert
        assertTrue(result);

    }

    @Test 
    void testContainsMissingItem() {
        //arrange
        inventory.add(apple);
       //act
        boolean result = inventory.contains(knife);
        //assert
        assertFalse(result);
    }

    @Test
    void testGet() {
        //arrange
        inventory.add(apple);
        inventory.add(knife);
        //act
        Item result = inventory.get(1);
        //assert
        assertSame(knife, result);
        assertNull(inventory.get(4));

    }

    @Test 
    void testGetOutOfBounds() {
        //arrange
        inventory.add(apple);
        inventory.add(knife);
        //act and assert
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> 
            inventory.get(5));
        
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> 
            inventory.get(-1));
        };
    

    @Test
    void testGetItems() {
        //arrange
        inventory.add(apple);
        inventory.add(knife);
        //act
        Item[] result = inventory.getItems();
        //assert
        assertArrayEquals(new Item[]{apple, knife, null, null, null}, result);

    }

    @Test 
    void testGetItemsEmptyInventory() {
        //act
        Item[] result = inventory.getItems();
        //assert
        assertArrayEquals(new Item[]{null, null, null, null, null}, result);

    }

   

    @Test
    void testIsFull() {
        //arrange
        for(int i = 0; i<5; i++) {
            inventory.add(apple);
        }
        //act
        boolean result = inventory.isFull();
        //assert
        assertTrue(result);

    }

    @Test
    void testRemove() {
        //arrange
        inventory.add(apple);
        inventory.add(knife);
        //act
        inventory.remove(knife);
        //assert
        assertSame(apple, inventory.get(0));
        assertNull(inventory.get(1));
        assertFalse(inventory.contains(knife));
        assertFalse(inventory.isFull());

    }

    @Test 
    void testRemoveMissingItem() {
        //arrange
        inventory.add(apple);
        Item[] itemsBefore = inventory.getItems().clone();
        //act
        inventory.remove(knife);
        //assert
        assertArrayEquals(itemsBefore, inventory.getItems());
    }
}
