package recursion_fractals_and_2d_graphics;

public class Recursion {

    // TODO 01 — recursive addition of two integers
    public static int sum(int a, int b) {
        if (b == 0) return a;
        return sum(++a, --b);
    }

    // TODO 02 — recursive iteration without loops
    public static void times(int i) {
        if (i == 0) return;
        System.out.println("i: " + i);
        times(--i);
    }

    // TODO 04 — recursive sum of array elements
    public static int array_sum(int[] tab, int i) {
        if (i == 0) return tab[0];
        return tab[i] + array_sum(tab, i - 1);
    }

    // TODO 03 — main: invoke sum and times, then array_sum
    public static void main(String[] args) {
        System.out.println(sum(6, 9));              // expects 15
        times(10);                                  // expects i: 10 … i: 1
        int[] numbers = {1, 2, 3, 4, 5};
        int result = array_sum(numbers, numbers.length - 1);
        System.out.println("Array sum: " + result); // expects Array sum: 15
    }
}
