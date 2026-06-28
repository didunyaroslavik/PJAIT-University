package performance_analysis_of_operations_on_java_collections;

import java.util.Collection;
import java.util.List;
import java.util.Random;

// Problem 3: Build a collection of unique elements from a dataset with many duplicates
// List types must call contains() before each add → O(n²) total
// Set types handle uniqueness internally → O(n) for HashSet, O(n log n) for TreeSet
public class Problem3Test implements PerformanceTask {

    private final Collection<Integer> collection;
    private final String collectionName;
    private final int[] data;
    private final boolean isList;

    public Problem3Test(Collection<Integer> collection, int dataSize, int trials) {
        this.collection = collection;
        this.collectionName = collection.getClass().getSimpleName();
        this.isList = collection instanceof List;
        Random rand = new Random(7);

        this.data = new int[dataSize];
        for (int i = 0; i < dataSize; i++)
            data[i] = rand.nextInt(dataSize / 2); // ~50% duplicates guaranteed
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
        collection.clear();
        for (int val : data) {
            if (isList) {
                // Lists have no built-in uniqueness: manual contains() check is O(n)
                if (!collection.contains(val)) collection.add(val);
            } else {
                // Sets reject duplicates internally and efficiently
                collection.add(val);
            }
        }
    }
}
