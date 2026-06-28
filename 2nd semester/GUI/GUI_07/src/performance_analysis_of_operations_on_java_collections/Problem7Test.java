package performance_analysis_of_operations_on_java_collections;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

// Problem 7: Traverse a list with an iterator and remove every 4th element
// LinkedList: iterator removal is O(1) — just unlinking a node
// ArrayList: iterator removal is O(n) — must shift all elements after the removed index
public class Problem7Test implements PerformanceTask {

    private final List<Integer> list;
    private final int dataSize;

    public Problem7Test(List<Integer> list, int dataSize, int trials) {
        this.list = list;
        this.dataSize = dataSize;
        this.generateTestData(dataSize, list);
    }

    @Override
    public List<Integer> generateTestData(int size, List<Integer> testData) {
        testData.clear();
        Random rand = new Random(17);
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
        generateTestData(dataSize, list);   // reset to full list each benchmark run
        Iterator<Integer> it = list.iterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            if (count % 4 == 0) it.remove();
            count++;
        }
    }
}
