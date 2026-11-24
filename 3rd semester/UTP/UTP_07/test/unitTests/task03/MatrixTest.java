package unitTests.task03;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public
    class MatrixTest {

    @Test
    public void testIsSquare() {
        Matrix square = new Matrix(new int[][]{
            {1, 2},
            {3, 4}
        });
        Matrix notSquare = new Matrix(new int[][]{
            {1, 2, 3},
            {4, 5, 6}
        });

        assertTrue(square.isSquare());
        assertFalse(notSquare.isSquare());
    }

    @Test
    public void testTranspose() {
        Matrix m = new Matrix(new int[][]{
            {1, 2, 3},
            {4, 5, 6}
        });
        int[][] expected = {
            {1, 4},
            {2, 5},
            {3, 6}
        };

        assertArrayEquals(expected, m.transpose().getData());
    }

    @Test
    public void testAdditionDimensionMismatch() {
        Matrix a = new Matrix(new int[][]{
            {1, 2},
            {3, 4}
        });
        Matrix b = new Matrix(new int[][]{
            {1, 2, 3},
            {4, 5, 6}
        });

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> a.add(b)
        );
        assertEquals("Matrices must have the same dimensions!", ex.getMessage());
    }

    @Test
    public void testMultiplicationDimensionMismatch() {
        // 2x3 * 2x3 -> columns of A (3) != rows of B (2)
        Matrix a = new Matrix(new int[][]{
            {1, 2, 3},
            {4, 5, 6}
        });
        Matrix b = new Matrix(new int[][]{
            {1, 2, 3},
            {4, 5, 6}
        });

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> a.multiply(b)
        );
        assertEquals("Invalid matrix dimensions for multiplication!", ex.getMessage());
    }

    @Test
    public void testAddition() {
        Matrix a = new Matrix(new int[][]{
            {1, 2},
            {3, 4}
        });
        Matrix b = new Matrix(new int[][]{
            {5, 6},
            {7, 8}
        });
        int[][] expected = {
            {6, 8},
            {10, 12}
        };

        assertArrayEquals(expected, a.add(b).getData());
    }

    @Test
    public void testMultiplication() {
        // 2x3 * 3x2 = 2x2
        Matrix a = new Matrix(new int[][]{
            {1, 2, 3},
            {4, 5, 6}
        });
        Matrix b = new Matrix(new int[][]{
            {7, 8},
            {9, 10},
            {11, 12}
        });
        int[][] expected = {
            {58, 64},
            {139, 154}
        };

        assertArrayEquals(expected, a.multiply(b).getData());
    }
}
