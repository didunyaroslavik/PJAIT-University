package performance_analysis_of_operations_on_java_collections;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

// Problem 10: Queue operations — offer (enqueue) then immediately poll (dequeue)
// ArrayDeque: circular array buffer, no object allocation per element → fastest queue
// LinkedList: allocates a new Node object on every offer() → GC pressure, slower
// ArrayList: remove(0) must shift all remaining elements even for size-1 list → more overhead
// Java documentation recommends ArrayDeque as the default Queue implementation
public class Problem10Test implements PerformanceTask {

    private final String collectionName;
    private final int trials;
    private final int[] values;
    private final int type;

    public Problem10Test(Object collection, int dataSize, int trials) {
        this.collectionName = collection.getClass().getSimpleName();
        this.trials = trials;
        if (collection instanceof ArrayDeque)
            type = 0;
        else if (collection instanceof LinkedList)
            type = 1;
        else
            type = 2;
        Random rand = new Random(37);
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
        switch (type) {
            case 0 -> {
                Queue<Integer> q = new ArrayDeque<>();
                for (int v : values) {
                    q.offer(v);
                    q.poll();
                }
            }
            case 1 -> {
                Queue<Integer> q = new LinkedList<>();
                for (int v : values) {
                    q.offer(v);
                    q.poll();
                }
            }
            case 2 -> {
                ArrayList<Integer> al = new ArrayList<>();
                for (int v : values) {
                    al.add(v);
                    if (!al.isEmpty())
                        al.remove(0);
                }
            }
        }
    }
}
