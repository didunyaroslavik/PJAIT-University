package generic_transformation;

import java.util.Arrays;

@FunctionalInterface
interface Transform<T, R> {
    R apply(T s);
}

// Implements the interface for T=String and R=Integer returning the length of the string
class StrToInt implements Transform<String, Integer> {
    @Override
    public Integer apply(String s) {
        return s.length();
    }
}

public class GenTrans {

    // Fills the out array by applying the trans.apply() method to each element from the in array
    private static <T, R> void transform(T[] in, R[] out, Transform<T, R> trans) {
        for (int i = 0; i < in.length; i++) {
            out[i] = trans.apply(in[i]);
        }
    }

    public static void main (String[] args) {
        String[]  sin = {"Alice", "Sue", "Janet", "Bea"};
        System.out.println(Arrays.toString(sin) + '\n');

        Integer[] iout = new Integer[sin.length];
        // 1. Using an object of StrToInt type
        transform(sin, iout, new StrToInt());
        System.out.println(Arrays.toString(iout));

        Character[] cout = new Character[sin.length];
        // 2. Using an object of an anonymous class returning the first character
        transform(sin, cout, new Transform<String, Character>() {
            @Override
            public Character apply(String s) {
                return s.charAt(0);
            }
        });
        System.out.println(Arrays.toString(cout));

        String[] sout = new String[sin.length];
        // 3. Using a lambda expression to transform strings to uppercase
        transform(sin, sout, s -> s.toUpperCase());
        System.out.println(Arrays.toString(sout));
    }
}