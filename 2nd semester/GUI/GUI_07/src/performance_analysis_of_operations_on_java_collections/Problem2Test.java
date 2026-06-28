package performance_analysis_of_operations_on_java_collections;

import java.util.List;
import java.util.Random;

// Problem 2: Insert at the beginning — list.add(0, value)
// Best collection: LinkedList (O(1)); ArrayList is O(n) because it must shift all elements
public class Problem2Test implements PerformanceTask {

    private final List<Integer> list;
    private final int trials;
    private final int[] insertValues;

    public Problem2Test(List<Integer> list, int dataSize, int trials) {
        this.list = this.generateTestData(dataSize, list);
        this.trials = trials;
        Random rand = new Random(99);
        this.insertValues = new int[trials];
        for (int i = 0; i < trials; i++)
            insertValues[i] = rand.nextInt(1_000_000);
    }

    @Override
    public List<Integer> generateTestData(int size, List<Integer> testData) {
        Random rand = new Random(1);
        for (int i = 0; i < size; i++)
            testData.add(rand.nextInt(1_000_000));
        return testData;
    }

    @Override
    public String getListName() {
        return list.getClass().getSimpleName();
    }

    @Override
    public void execute() {
        for (int i = 0; i < trials; i++) {
            list.add(0, insertValues[i]);
        }
    }
}
