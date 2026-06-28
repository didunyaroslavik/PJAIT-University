package performance_analysis_of_operations_on_java_collections;

import java.util.concurrent.TimeUnit;

public class BenchmarkRunner {

    public void runTest(PerformanceTask task) {
        long startTime = System.nanoTime();
        task.execute();
        long endTime = System.nanoTime();

        long durationNano = endTime - startTime;
        long durationMicro = TimeUnit.NANOSECONDS.toMicros(durationNano);

        System.out.printf("  %-20s -> %,7d us%n", task.getListName(), durationMicro);
    }
}
