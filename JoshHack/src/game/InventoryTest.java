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
        inventory.add(null);
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

    }

    @Test
    void testGet() {

    }

    @Test
    void testGetItems() {

    }

    @Test
    void testIsFull() {

    }

    @Test
    void testRemove() {

    }
}
