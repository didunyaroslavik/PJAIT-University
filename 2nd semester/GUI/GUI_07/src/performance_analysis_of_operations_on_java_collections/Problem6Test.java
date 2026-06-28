package performance_analysis_of_operations_on_java_collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;

// Problem 6: Priority queue simulation — offer (insert with priority) + poll (remove highest)
// PriorityQueue is purpose-built: O(log n) offer/poll using a binary heap
// TreeSet can simulate a priority queue: O(log n) add/pollFirst but more node allocation overhead
// ArrayList sorted manually: O(n log n) sort + O(n) remove(0) → slowest
public class Problem6Test implements PerformanceTask {

    private final String collectionName;
    private final int[] priorities;
    private final int trials;
    private final int type;

    public Problem6Test(Collection<Integer> collection, int dataSize, int trials) {
        this.collectionName = collection.getClass().getSimpleName();
        this.trials = trials;
        if (collection instanceof PriorityQueue)
            type = 0;
        else if (collection instanceof TreeSet)
            type = 1;
        else
            type = 2;

        Random rand = new Random(11);
        this.priorities = new int[trials * 2];
        for (int i = 0; i < priorities.length; i++)
            priorities[i] = rand.nextInt(10_000);
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
            case 0 -> runPriorityQueue();
            case 1 -> runTreeSet();
            case 2 -> runArrayList();
        }
    }

    private void runPriorityQueue() {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        for (int i = 0; i < trials; i++) {
            pq.offer(priorities[i * 2]);
            pq.poll();
        }
    }

    private void runTreeSet() {
        TreeSet<Integer> ts = new TreeSet<>(Collections.reverseOrder());
        int unique = 0;
        for (int i = 0; i < trials; i++) {
            ts.add(priorities[i * 2] * 1000 + unique++);
            ts.pollFirst();
        }
    }

    private void runArrayList() {
        ArrayList<Integer> al = new ArrayList<>();
        for (int i = 0; i < trials; i++) {
            al.add(priorities[i * 2]);
            al.sort(Collections.reverseOrder());
            al.remove(0);
        }
    }
}
