package unitTests.task05;

import java.util.Random;

public
    class Main {

    public static void bogoSort(int[] array) {
        Random random = new Random();
        while (!isSorted(array)) {
            shuffle(array, random);
        }
    }

    public static boolean isSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++)
            if (array[i] > array[i + 1])
                return false;
        return true;
    }

    public static void shuffle(int[] array, Random random) {
        for (int i = 0; i < array.length; i++) {
            int randomIndex = random.nextInt(array.length);
            int temp = array[i];
            array[i] = array[randomIndex];
            array[randomIndex] = temp;
        }
    }

    public static int[] generateTestArray(){
        Random random = new Random(1000);
        int size = 11;

        int[] array = new int[size];

        for(int i=0; i<size; i++)
            array[i] = random.nextInt();
        return array;
    }

    public static void main(String[] args) {

        int[] array = Main.generateTestArray();

        System.out.println("Przed sortowaniem:");
        for (int num : array)
            System.out.print(num + " ");


        bogoSort(array);

        System.out.println("\nPo sortowaniu:");
        for (int num : array)
            System.out.print(num + " ");

    }

}


