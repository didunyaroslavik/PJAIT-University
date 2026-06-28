package performance_analysis_of_operations_on_java_collections;

import java.util.*;
import java.util.ArrayDeque;
import java.util.Stack;

public class Main {

    private static final int DATA_SIZE   = 100_000;
    private static final int TEST_TRIALS = 20_000;

    public static void main(String[] args) {
        BenchmarkRunner runner = new BenchmarkRunner();

        // ── Problem 1: Random access by index ──────────────────────────────────
        printHeader(1,
                "Random Access by Index",
                "list.get(random_index)",
                "ArrayList O(1) vs LinkedList O(n)");
        runner.runTest(new Problem1Test(new ArrayList<>(),  DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem1Test(new LinkedList<>(), DATA_SIZE, TEST_TRIALS));

        // ── Problem 2: Insert at the beginning ─────────────────────────────────
        printHeader(2,
                "Insert at the Beginning",
                "list.add(0, value)",
                "LinkedList O(1) vs ArrayList O(n) shift");
        runner.runTest(new Problem2Test(new ArrayList<>(),  DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem2Test(new LinkedList<>(), DATA_SIZE, TEST_TRIALS));

        // ── Problem 3: Build a unique-element collection ────────────────────────
        printHeader(3,
                "Build Unique-Element Collection",
                "contains()+add() for Lists; add() for Sets",
                "Sets O(n) or O(n log n) vs Lists O(n²)");
        runner.runTest(new Problem3Test(new ArrayList<>(),     DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem3Test(new LinkedList<>(),    DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem3Test(new HashSet<>(),       DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem3Test(new LinkedHashSet<>(), DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem3Test(new TreeSet<>(),       DATA_SIZE, TEST_TRIALS));

        // ── Problem 4: Word-frequency counting ─────────────────────────────────
        printHeader(4,
                "Word-Frequency Counting",
                "map.merge(word, 1, Integer::sum)",
                "HashMap O(1) vs TreeMap O(log n)");
        runner.runTest(new Problem4Test(new HashMap<>(),       DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem4Test(new LinkedHashMap<>(), DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem4Test(new TreeMap<>(),       DATA_SIZE, TEST_TRIALS));

        // ── Problem 5: Insert + sort + iterate ─────────────────────────────────
        printHeader(5,
                "Insert, Sort and Iterate Sum",
                "add() + sort (Lists only) + iterate",
                "TreeSet/PriorityQueue avoid re-sorting; LinkedList sort is costly");
        runner.runTest(new Problem5Test(new ArrayList<>(),     DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem5Test(new LinkedList<>(),    DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem5Test(new TreeSet<>(),       DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem5Test(new PriorityQueue<>(), DATA_SIZE, TEST_TRIALS));

        // ── Problem 6: Priority-queue simulation ───────────────────────────────
        printHeader(6,
                "Priority Queue Simulation",
                "offer(priority) + poll() per round",
                "PriorityQueue O(log n); TreeSet O(log n) w/ more overhead; ArrayList O(n log n)");
        runner.runTest(new Problem6Test(new PriorityQueue<>(), DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem6Test(new TreeSet<>(),       DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem6Test(new ArrayList<>(),     DATA_SIZE, TEST_TRIALS));

        // ── Problem 7: Iterator removal of every 4th element ───────────────────
        printHeader(7,
                "Iterator Removal (every 4th element)",
                "iterator.next() + iterator.remove() every 4 steps",
                "LinkedList O(1) removal; ArrayList O(n) element shift per removal");
        runner.runTest(new Problem7Test(new ArrayList<>(),  DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem7Test(new LinkedList<>(), DATA_SIZE, TEST_TRIALS));

        // ── Problem 8: Membership lookup — contains() ──────────────────────────
        printHeader(8,
                "Membership Lookup — contains()",
                "collection.contains(value), 50% hit rate",
                "HashSet O(1); TreeSet O(log n); Lists O(n) linear scan");
        runner.runTest(new Problem8Test(new ArrayList<>(),     DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem8Test(new LinkedList<>(),    DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem8Test(new HashSet<>(),       DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem8Test(new LinkedHashSet<>(), DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem8Test(new TreeSet<>(),       DATA_SIZE, TEST_TRIALS));

        // ── Problem 9: Stack push + pop ────────────────────────────────────────
        printHeader(9,
                "Stack Operations — push + pop",
                "push N elements then pop all",
                "ArrayDeque unsynchronized; Stack (legacy) acquires lock on every call");
        runner.runTest(new Problem9Test(new Stack<>(),     DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem9Test(new ArrayDeque<>(), DATA_SIZE, TEST_TRIALS));

        // ── Problem 10: Queue offer + poll ─────────────────────────────────────
        printHeader(10,
                "Queue Operations — offer + poll",
                "offer(v) then poll() for each element",
                "ArrayDeque circular buffer is fastest; LinkedList allocates nodes; ArrayList shifts");
        runner.runTest(new Problem10Test(new ArrayDeque<>(), DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem10Test(new LinkedList<>(), DATA_SIZE, TEST_TRIALS));
        runner.runTest(new Problem10Test(new ArrayList<>(),  DATA_SIZE, TEST_TRIALS));
    }

    private static void printHeader(int num, String title, String operation, String hint) {
        System.out.println("\n" + "=".repeat(70));
        System.out.printf("  Problem %d: %s%n", num, title);
        System.out.println("  Operation: " + operation);
        System.out.println("  Hint     : " + hint);
        System.out.println("=".repeat(70));
    }
}
