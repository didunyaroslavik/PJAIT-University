package function_composition;

import java.util.function.Function;

public class FunctionComposition {

    public static void main(String[] args) {
        // 1. Implement square as anonymous class that computes x squared
        Function<Integer, Integer> square = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x * x;
            }
        };

        // 2. Implement increment as lambda expression that adds 1
        Function<Integer, Integer> increment = x -> x + 1;

        // 3a. Compose 'square' and 'increment' using 'andThen'
        Function<Integer, Integer> squareAndIncrement = square.andThen(increment);

        // 3b. Test cases
        System.out.println(squareAndIncrement.apply(3));  // Expected: 10
        System.out.println(squareAndIncrement.apply(-2)); // Expected: 5

        // 3c. Explain the difference between using andThen() versus compose()
        /*
         * DIFFERENCE BETWEEN andThen() AND compose():
         * * f.andThen(g) applies 'f' to the input first, then applies 'g' to the result.
         * In our case: squareAndIncrement(3) -> square(3) = 9 -> increment(9) = 10.
         * * f.compose(g) applies 'g' to the input first, then applies 'f' to the result.
         * If we did square.compose(increment), it would increment first, then square the result.
         */

        // Trying with 'compose' to observe the difference
        Function<Integer, Integer> incrementAndSquare = square.compose(increment);
        System.out.println(incrementAndSquare.apply(3));  // Expected: 16 ((3 + 1) squared)
    }
}