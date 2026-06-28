package performance_analysis_of_operations_on_java_collections;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.Stack;

// Problem 9: Stack operations — push N elements then pop all of them
// Stack (legacy): extends Vector which is synchronized → lock acquisition overhead on every call
// ArrayDeque: modern, unsynchronized, array-based → significantly faster stack implementation
// Java documentation explicitly recommends ArrayDeque over Stack
public class Problem9Test implements PerformanceTask {

    private final String collectionName;
    private final int trials;
    private final int[] values;
    private final int type;

    public Problem9Test(Object collection, int dataSize, int trials) {
        this.collectionName = collection.getClass().getSimpleName();
        this.trials = trials;
        this.type = (collection instanceof Stack) ? 0 : 1;
        Random rand = new Random(31);
        this.values = new int[trials];
        for (int i = 0; i < trials; i++)
            values[i] = rand.nextInt(1_000_000);
    }

    @Override
    public List<Integer> generateTestData(int size, List<Integer> testData) {
        return testData;
    }

    @Override
    public String getListName() {
        return collectionName;
    }

    @Override
    public void execute() {
        if (type == 0) {
            Stack<Integer> stack = new Stack<>();
            for (int v : values)
                stack.push(v);
            while (!stack.isEmpty())
                stack.pop();
        } else {
            Deque<Integer> deque = new ArrayDeque<>();
            for (int v : values)
                deque.push(v);
            while (!deque.isEmpty())
                deque.pop();
        }
    }
}
