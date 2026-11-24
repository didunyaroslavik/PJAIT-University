package unitTests.task04;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public
    class PolynomialTest {

    @Test
    public void testAddition() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3}); // 3x^2 + 2x + 1
        Polynomial p2 = new Polynomial(new int[]{4, 0, -3}); // -3x^2 + 4

        Polynomial result = p1.add(p2); // (3x^2 + 2x + 1) + (-3x^2 + 4) = 2x + 5
        int[] expected = {5, 2, 0}; // 0x^2 + 2x + 5

        assertArrayEquals(expected, result.getCoefficients());
    }

    @Test
    public void testMultiplication() {
        Polynomial p1 = new Polynomial(new int[]{1, 2}); // 2x + 1
        Polynomial p2 = new Polynomial(new int[]{2, 1}); // x + 2

        Polynomial result = p1.multiply(p2); // (2x + 1)(x + 2) = 2x^2 + 5x + 2
        int[] expected = {2, 5, 2}; // 2x^2 + 5x + 2

        assertArrayEquals(expected, result.getCoefficients());
    }

    @Test
    public void testEvaluate() {
        Polynomial p = new Polynomial(new int[]{3, 0, 2}); // 2x^2 + 3
        double result = p.evaluate(2); // 2*(2^2) + 3 = 8 + 3 = 11
        assertEquals(11, result);
    }

    @Test
    public void testDerivative() {
        Polynomial p = new Polynomial(new int[]{3, 0, 2}); // 2x^2 + 3

        Polynomial result = p.derivative(); // Derivative: 4x + 0
        int[] expected = {0, 4}; // 4x + 0

        assertArrayEquals(expected, result.getCoefficients());
    }

}
