package performance_analysis_of_operations_on_java_collections;

import java.util.List;

public interface PerformanceTask {
    String getListName();
    void execute();
    List<Integer> generateTestData(int size, List<Integer> testData);
}
