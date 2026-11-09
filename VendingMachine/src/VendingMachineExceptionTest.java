import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class VendingMachineExceptionTest {

    @Test 
    void testExceptionMessage() {
        //arrange
        String message = "This is an invalid slot code";
        //act
        VendingMachineException exception = new VendingMachineException(message);
        //assert
        assertEquals(message, exception.getMessage());
    }

     @Test 
    void testExceptionWithNoMessage() {
        //act
        VendingMachineException exception = new VendingMachineException();
        //assert
        assertNull(exception.getMessage());
    }

   

   

   
}
