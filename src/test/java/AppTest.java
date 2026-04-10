import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    void testOrderValidationSuccess() {
        assertTrue(App.validateOrder(5, 50.0), "Order should be valid with items and positive amount");
    }

    @Test
    void testOrderValidationFailure() {
        assertFalse(App.validateOrder(0, 0.0), "Order should be invalid with 0 items");
        assertFalse(App.validateOrder(3, -10.0), "Order should be invalid with negative amount");
    }

    @Test
    void testDeliverySlotValidationSuccess() {
        assertTrue(App.validateDeliverySlot(10), "10 AM should be a valid slot");
        assertTrue(App.validateDeliverySlot(14), "2 PM should be a valid slot");
    }

    @Test
    void testDeliverySlotValidationFailure() {
        assertFalse(App.validateDeliverySlot(6), "6 AM should be an invalid slot (too early)");
        assertFalse(App.validateDeliverySlot(22), "10 PM should be an invalid slot (too late)");
    }
}
