package unitTests.task05;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public
    class MainTest {

    // the array that generateTestArray() should always return
    // (Random with the same seed 1000 always gives the same numbers)
    private static final int[] EXPECTED = Main.generateTestArray();

    // runs the check many times by itself, in a loop
    @Test
    public void simpleTest() {
        for (int i = 0; i < 10; i++) {
            assertArrayEquals(EXPECTED, Main.generateTestArray(),
                "generateTestArray() returned a different array in run " + (i + 1));
        }
    }

    // the same check, but JUnit repeats the whole test 10 times
    // (each repetition is shown as a separate test in the results)
    @RepeatedTest(10)
    public void simpleTestR() {
        assertArrayEquals(EXPECTED, Main.generateTestArray());
    }

    @Test
    public void manualPerfTest() {
        int[] array = Main.generateTestArray();

        long start = System.nanoTime();
        Main.bogoSort(array);
        long end = System.nanoTime();

        double ms = (end - start) / 1_000_000.0;
        System.out.printf("bogoSort of %d elements took %.3f ms (%.3f s)%n",
            array.length, ms, ms / 1000);

        assertTrue(Main.isSorted(array));
    }

    // identical to simpleTestR, but tagged - tagged tests can be run
    // or skipped as a group (e.g. --include-tag performance)
    @RepeatedTest(10)
    @Tag("performance")
    public void autoPerfTest() {
        assertArrayEquals(EXPECTED, Main.generateTestArray());
    }

    // fails if the test takes more than 1 second.
    // SEPARATE_THREAD is needed: by default (SAME_THREAD) JUnit only tries to
    // interrupt the test, and bogoSort never checks for interruption, so the
    // test would still run until the array is sorted and only fail after that.
    // With SEPARATE_THREAD JUnit stops waiting after 1 second and fails at once.
    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    public void finalTest() {
        int[] array = Main.generateTestArray();
        Main.bogoSort(array);
        assertTrue(Main.isSorted(array));
    }
}
