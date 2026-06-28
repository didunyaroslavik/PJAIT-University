package performance_analysis_of_operations_on_java_collections;

import java.util.Collection;
import java.util.List;
import java.util.Random;

// Problem 8: Membership lookup — collection.contains(value)
// Queries are 50% values that exist and 50% that do not, to measure both hit and miss costs
// ArrayList / LinkedList: O(n) linear scan per query → very slow at large sizes
// HashSet / LinkedHashSet: O(1) average via hashing → fastest
// TreeSet: O(log n) via binary search on sorted tree → good middle ground
public class Problem8Test implements PerformanceTask {

    private final Collection<Integer> collection;
    private final String collectionName;
    private final int[] queries;

    public Problem8Test(Collection<Integer> collection, int dataSize, int trials) {
        this.collection = collection;
        this.collectionName = collection.getClass().getSimpleName();
        this.generateTestData(dataSize, null);

        Random rand = new Random(23);
        this.queries = new int[trials];
        int[] dataArr = collection.stream().mapToInt(Integer::intValue).toArray();
        for (int i = 0; i < trials; i++) {
            if (i % 2 == 0)
                queries[i] = dataArr[rand.nextInt(dataArr.length)];   // existing value
            else
                queries[i] = rand.nextInt(2_000_000) + 1_000_001;     // value outside dataset range
        }
    }

    @Override
    public List<Integer> generateTestData(int size, List<Integer> testData) {
        collection.clear();
        Random rand = new Random(23);
        for (int i = 0; i < size; i++)
            collection.add(rand.nextInt(1_000_000));
        return testData;
    }

    @Override
    public String getListName() {
        return collectionName;
    }

    @Override
    public void execute() {
        int found = 0;
        for (int q : queries) {
            if (collection.contains(q))
                found++;
        }

        // Prevent JIT from eliminating the loop
        if (found < 0)
            System.out.print("");
    }
}
