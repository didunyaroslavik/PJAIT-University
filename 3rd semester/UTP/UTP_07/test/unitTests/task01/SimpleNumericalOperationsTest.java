package unitTests.task01;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public
    class SimpleNumericalOperationsTest {

    private final SimpleNumericalOperations ops = new SimpleNumericalOperations();

    @Test
    public void testAddition() {
        assertEquals(5, ops.add(2, 3));
        assertEquals(-1, ops.add(2, -3));
        assertEquals(0, ops.add(0, 0));
        assertEquals(-7, ops.add(-3, -4));
    }

    // all states: positive number, negative number and zero (neither)
    @Test
    public void testPosNeg() {
        assertAll("isPositive / isNegative",
            () -> assertTrue(ops.isPositive(5), "5 should be positive"),
            () -> assertFalse(ops.isNegative(5), "5 should not be negative"),

            () -> assertTrue(ops.isNegative(-5), "-5 should be negative"),
            () -> assertFalse(ops.isPositive(-5), "-5 should not be positive"),

            () -> assertFalse(ops.isPositive(0), "0 should not be positive"),
            () -> assertFalse(ops.isNegative(0), "0 should not be negative"),

            () -> assertTrue(ops.isPositive(Integer.MAX_VALUE), "MAX_VALUE should be positive"),
            () -> assertTrue(ops.isNegative(Integer.MIN_VALUE), "MIN_VALUE should be negative")
        );
    }
}
