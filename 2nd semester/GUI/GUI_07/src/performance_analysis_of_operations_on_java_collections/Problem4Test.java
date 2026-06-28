package performance_analysis_of_operations_on_java_collections;

import java.util.List;
import java.util.Map;
import java.util.Random;

// Problem 4: Word frequency counting — map.merge(word, 1, Integer::sum)
// Best collection: HashMap (O(1) per operation); TreeMap is O(log n) due to tree structure
public class Problem4Test implements PerformanceTask {

    private final Map<String, Integer> map;
    private final String[] words;

    public Problem4Test(Map<String, Integer> map, int dataSize, int trials) {
        this.map = map;
        Random rand = new Random(3);
        String[] dictionary = new String[500];
        for (int i = 0; i < dictionary.length; i++)
            dictionary[i] = "word_" + i;

        this.words = new String[dataSize];
        for (int i = 0; i < dataSize; i++)
            this.words[i] = dictionary[rand.nextInt(dictionary.length)];
    }

    @Override
    public List<Integer> generateTestData(int size, List<Integer> testData) {
        return testData;
    }

    @Override
    public String getListName() {
        return map.getClass().getSimpleName();
    }

    @Override
    public void execute() {
        map.clear();
        for (String word : words)
            map.merge(word, 1, Integer::sum);
    }
}
