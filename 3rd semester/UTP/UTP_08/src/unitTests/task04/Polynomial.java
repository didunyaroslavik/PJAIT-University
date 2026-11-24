package unitTests.task04;

import java.util.Arrays;

// coefficients[i] is the coefficient for x^i
// e.g. 3x^2 + 5x + 2  ->  [2, 5, 3]
public
    class Polynomial {

    private final int[] coefficients;

    public Polynomial(int[] coefficients) {
        this.coefficients = coefficients.clone();
    }

    public int[] getCoefficients() {
        return coefficients.clone();
    }

    // sum of coefficients with the same power,
    // the result has the length of the longer polynomial
    public Polynomial add(Polynomial other) {
        int[] result = new int[Math.max(this.coefficients.length, other.coefficients.length)];

        for (int i = 0; i < this.coefficients.length; i++)
            result[i] += this.coefficients[i];
        for (int i = 0; i < other.coefficients.length; i++)
            result[i] += other.coefficients[i];

        return new Polynomial(result);
    }

    // every term with every term: a_i x^i * b_j x^j = a_i*b_j x^(i+j)
    // degree n and degree m -> degree n+m -> n+m+1 coefficients
    public Polynomial multiply(Polynomial other) {
        int[] result = new int[this.coefficients.length + other.coefficients.length - 1];

        for (int i = 0; i < this.coefficients.length; i++)
            for (int j = 0; j < other.coefficients.length; j++)
                result[i + j] += this.coefficients[i] * other.coefficients[j];

        return new Polynomial(result);
    }

    // Horner's method: a0 + x(a1 + x(a2 + ... + x*an))
    public double evaluate(double x) {
        double result = 0;
        for (int i = coefficients.length - 1; i >= 0; i--)
            result = result * x + coefficients[i];
        return result;
    }

    // power rule: (a_i x^i)' = i * a_i x^(i-1)
    public Polynomial derivative() {
        if (coefficients.length <= 1)
            return new Polynomial(new int[]{0}); // derivative of a constant is 0

        int[] result = new int[coefficients.length - 1];
        for (int i = 1; i < coefficients.length; i++)
            result[i - 1] = i * coefficients[i];

        return new Polynomial(result);
    }

    @Override
    public String toString() {
        return Arrays.toString(coefficients);
    }
}
