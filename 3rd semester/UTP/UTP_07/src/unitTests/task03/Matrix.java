
package unitTests.task03;

public class Matrix {

    private int[][] data;

    // Constructor
    public Matrix(int[][] data) {
        this.data = data;
    }

    // Addition of two matrices
    public Matrix add(Matrix other) {
        if (this.data.length != other.data.length || this.data[0].length != other.data[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions!");
        }

        int[][] result = new int[data.length][data[0].length];

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                result[i][j] = this.data[i][j] + other.data[i][j];
            }
        }

        return new Matrix(result);
    }

    // Multiplication of two matrices
    public Matrix multiply(Matrix other) {
        if (this.data[0].length != other.data.length) {
            throw new IllegalArgumentException("Invalid matrix dimensions for multiplication!");
        }

        int[][] result = new int[this.data.length][other.data[0].length];

        for (int i = 0; i < this.data.length; i++) {
            for (int j = 0; j < other.data[0].length; j++) {
                for (int k = 0; k < this.data[0].length; k++) {
                    result[i][j] += this.data[i][k] * other.data[k][j];
                }
            }
        }

        return new Matrix(result);
    }

    // Matrix transposition
    public Matrix transpose() {
        int[][] result = new int[this.data[0].length][this.data.length];

        for (int i = 0; i < this.data.length; i++) {
            for (int j = 0; j < this.data[0].length; j++) {
                result[j][i] = this.data[i][j];
            }
        }

        return new Matrix(result);
    }

    // Check whether the matrix is square
    public boolean isSquare() {
        return this.data.length == this.data[0].length;
    }

    // Return matrix data for testing
    public int[][] getData() {
        return this.data;
    }
}
